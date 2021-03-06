/***************************************************************************

  Capcom System 1
  ===============

  Driver provided by:
  Paul Leaman (paul@vortexcomputing.demon.co.uk)

  M680000 for game, Z80, YM-2151 and OKIM6295 for sound.

  68000 clock speeds are unknown for all games (except where commented)

merged Street Fighter Zero for MESS

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class cpschngr
{
	
	
	
	
	public static ReadHandlerPtr cps1_input2_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int buttons=readinputport(6);
		return buttons << 8 | buttons;
	} };
	
	public static ReadHandlerPtr cps1_input3_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
	    int buttons=readinputport(7);
		return buttons << 8 | buttons;
	} };
	
	
	static int cps1_sound_fade_timer;
	
	public static WriteHandlerPtr cps1_snd_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UBytePtr RAM = memory_region(REGION_CPU2);
		int length = memory_region_length(REGION_CPU2) - 0x10000;
		int bankaddr;
	
		bankaddr = (data * 0x4000) & (length-1);
		cpu_setbank(1,&RAM[0x10000 + bankaddr]);
	
	if ((data & 0xfe)) logerror("%04x: write %02x to f004\n",cpu_get_pc(),data);
	} };
	
	public static WriteHandlerPtr cps1_sound_fade_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cps1_sound_fade_timer=data;
	} };
	
	public static ReadHandlerPtr cps1_snd_fade_timer_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cps1_sound_fade_timer;
	} };
	
	public static ReadHandlerPtr cps1_input_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int control=readinputport (offset/2);
		return (control<<8) | control;
	} };
	
	public static ReadHandlerPtr cps1_player_input_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (readinputport(offset + 4) + (readinputport(offset+1 + 4)<<8));
	} };
	
	static int dial[2];
	
	public static ReadHandlerPtr forgottn_dial_0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return ((readinputport(6) - dial[0]) >> (4*offset)) & 0xff;
	} };
	
	public static ReadHandlerPtr forgottn_dial_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return ((readinputport(7) - dial[1]) >> (4*offset)) & 0xff;
	} };
	
	public static WriteHandlerPtr forgottn_dial_0_reset_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		dial[0] = readinputport(6);
	} };
	
	public static WriteHandlerPtr forgottn_dial_1_reset_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		dial[1] = readinputport(7);
	} };
	
	public static WriteHandlerPtr cps1_coinctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if ((data & 0xff000000) == 0)
		{
	/*
	{
		char baf[40];
		sprintf(baf,"%04x",data);
	    usrintf_showmessage(baf);
	}
	*/
			coin_lockout_w.handler(0,~data & 0x0400);
			coin_lockout_w.handler(1,~data & 0x0800);
			coin_counter_w.handler(0,data & 0x0100);
			coin_counter_w.handler(1,data & 0x0200);
		}
	} };
	
	WRITE_HANDLER ( cpsq_coinctrl2_w )
	{
		if ((data & 0xff000000) == 0)
		{
			coin_lockout_w(2,~data & 0x0002);
			coin_lockout_w(3,~data & 0x0008);
			coin_counter_w(2,data & 0x0001);
			coin_counter_w(3,data & 0x0004);
	/*
	  	{
	       char baf[40];
	       sprintf(baf,"0xf1c004=%04x", data);
	       usrintf_showmessage(baf);
	       }
	*/
	    }
	}
	
	READ_HANDLER ( cps1_protection_ram_r )
	{
		/*
		   Protection (slammasters):
	
		   The code does a checksum on an area of memory. I have no idea what
		   this memory is. I have no idea whether it is RAM based or hard-wired.
	
		   The code adds the low bytes of 0x415 words starting at 0xf0e000
	
		   The result is ANDed with 0xffffff00 and then multiplied by 2. This
		   value is stored and used throughout the game to calculate the
		   base offset of the source scroll ROM data.
	
		   The sum of the low bytes of the first 0x415 words starting at
		   address 0xf0e000 should be 0x1df00
	
		   In the absence of any real data, a rough calculation will do the
		   job.
		*/
	
		if (offset < (0x411*2))
		{
			/*
				0x411 * 0x76 = 0x1dfd6  (which is close enough)
			*/
			return 0x76;
		}
		else
		{
			return 0;
		}
	}
	
	public static InterruptPtr cps1_interrupt = new InterruptPtr() { public int handler() 
	{
		/* Strider also has a IRQ4 handler. It is input port related, but the game */
		/* works without it (maybe it's used to multiplex controls). It is the */
		/* *only* game to have that. */
		return 2;
	} };
	
	/********************************************************************
	*
	*  Q Sound
	*  =======
	*
	********************************************************************/
	
	static struct QSound_interface qsound_interface =
	{
		QSOUND_CLOCK,
		REGION_SOUND1,
		{ 100,100 }
	};
	
	static UBytePtr qsound_sharedram;
	
	public static InterruptPtr cps1_qsound_interrupt = new InterruptPtr() { public int handler() 
	{
		/* kludge to pass the sound board test with sound disabled */
		if (Machine.sample_rate == 0)
			qsound_sharedram[0xfff] = 0x77;
	
		return 2;
	} };
	
	public static ReadHandlerPtr qsound_sharedram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return qsound_sharedram[offset / 2] | 0xff00;
	} };
	
	public static WriteHandlerPtr qsound_sharedram1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		qsound_sharedram[offset / 2] = data;
	} };
	
	public static WriteHandlerPtr qsound_banksw_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/*
		Z80 bank register for music note data. It's odd that it isn't encrypted
		though.
		*/
		UBytePtr RAM = memory_region(REGION_CPU2);
		int bankaddress=0x10000+((data&0x0f)*0x4000);
		if (bankaddress >= memory_region_length(REGION_CPU2))
		{
	
			{
				logerror("WARNING: Q sound bank overflow (%02x)\n", data);
			}
			bankaddress=0x10000;
		}
		cpu_setbank(1, &RAM[bankaddress]);
	} };
	
	
	/********************************************************************
	*
	*  EEPROM
	*  ======
	*
	*   The EEPROM is accessed by a serial protocol using the register
	*   0xf1c006
	*
	********************************************************************/
	
	static EEPROM_interface qsound_eeprom_interface = new EEPROM_interface
	(
		7,		/* address bits */
		8,		/* data bits */
		"0110",	/*  read command */
		"0101",	/* write command */
		"0111"	/* erase command */
	);
	
	static EEPROM_interface pang3_eeprom_interface = new EEPROM_interface
	(
		6,		/* address bits */
		16,		/* data bits */
		"0110",	/*  read command */
		"0101",	/* write command */
		"0111"	/* erase command */
	);
	
	public static nvramPtr qsound_nvram_handler  = new nvramPtr() { public void handler(Object file, int read_or_write) 
	{
		if (read_or_write != 0)
			EEPROM_save(file);
		else
		{
			EEPROM_init(&qsound_eeprom_interface);
	
			if (file != 0)
				EEPROM_load(file);
		}
	} };
	
	public static nvramPtr pang3_nvram_handler  = new nvramPtr() { public void handler(Object file, int read_or_write) 
	{
		if (read_or_write != 0)
			EEPROM_save(file);
		else
		{
			EEPROM_init(&pang3_eeprom_interface);
	
			if (file != 0)
				EEPROM_load(file);
		}
	} };
	
	READ_HANDLER ( cps1_eeprom_port_r )
	{
		return EEPROM_read_bit();
	}
	
	WRITE_HANDLER ( cps1_eeprom_port_w )
	{
		/*
		bit 0 = data
		bit 6 = clock
		bit 7 = cs
		*/
		EEPROM_write_bit(data & 0x01);
		EEPROM_set_cs_line((data & 0x80) ? CLEAR_LINE : ASSERT_LINE);
		EEPROM_set_clock_line((data & 0x40) ? ASSERT_LINE : CLEAR_LINE);
	}
	
	
	
	static MemoryReadAddress cps1_readmem[] =
	{
		new MemoryReadAddress( 0x000000, 0x1fffff, MRA_ROM ), /* 68000 ROM */
		new MemoryReadAddress( 0x800000, 0x800003, cps1_player_input_r ), /* Player input ports */
		new MemoryReadAddress( 0x800010, 0x800013, cps1_player_input_r ), /* ?? */
		new MemoryReadAddress( 0x800018, 0x80001f, cps1_input_r ), /* Input ports */
		new MemoryReadAddress( 0x800020, 0x800021, MRA_NOP ), /* ? Used by Rockman ? */
		new MemoryReadAddress( 0x800052, 0x800055, forgottn_dial_0_r ), /* forgotten worlds */
		new MemoryReadAddress( 0x80005a, 0x80005d, forgottn_dial_1_r ), /* forgotten worlds */
		new MemoryReadAddress( 0x800176, 0x800177, cps1_input2_r ), /* Extra input ports */
		new MemoryReadAddress( 0x8001fc, 0x8001fd, cps1_input2_r ), /* Input ports (SF Rev E) */
		new MemoryReadAddress( 0x800100, 0x8001ff, cps1_output_r ),   /* Output ports */
		new MemoryReadAddress( 0x900000, 0x92ffff, MRA_BANK3 ),	/* SF2CE executes code from here */
		new MemoryReadAddress( 0xf0e000, 0xf0efff, cps1_protection_ram_r ), /* Slammasters protection */
		new MemoryReadAddress( 0xf18000, 0xf19fff, qsound_sharedram_r ),       /* Q RAM */
		new MemoryReadAddress( 0xf1c000, 0xf1c001, cps1_input2_r ),   /* Player 3 controls (later games) */
		new MemoryReadAddress( 0xf1c002, 0xf1c003, cps1_input3_r ),   /* Player 4 controls (later games - muscle bombers) */
		new MemoryReadAddress( 0xf1c006, 0xf1c007, cps1_eeprom_port_r ),
		new MemoryReadAddress( 0xff0000, 0xffffff, MRA_BANK2 ),   /* RAM */
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress cps1_writemem[] =
	{
		new MemoryWriteAddress( 0x000000, 0x1fffff, MWA_ROM ),      /* ROM */
		new MemoryWriteAddress( 0x800030, 0x800031, cps1_coinctrl_w ),
		new MemoryWriteAddress( 0x800040, 0x800041, forgottn_dial_0_reset_w ),
		new MemoryWriteAddress( 0x800048, 0x800049, forgottn_dial_1_reset_w ),
		new MemoryWriteAddress( 0x800180, 0x800181, soundlatch_w ),  /* Sound command */
		new MemoryWriteAddress( 0x800188, 0x800189, cps1_sound_fade_w ),
		new MemoryWriteAddress( 0x800100, 0x8001ff, cps1_output_w, cps1_output, cps1_output_size ),  /* Output ports */
		new MemoryWriteAddress( 0x900000, 0x92ffff, MWA_BANK3, cps1_gfxram, cps1_gfxram_size ),
		new MemoryWriteAddress( 0xf18000, 0xf19fff, qsound_sharedram1_w ),/* Q RAM */
		new MemoryWriteAddress( 0xf1c004, 0xf1c005, cpsq_coinctrl2_w ),   /* Coin control2 (later games) */
		new MemoryWriteAddress( 0xf1c006, 0xf1c007, cps1_eeprom_port_w ),
		new MemoryWriteAddress( 0xff0000, 0xffffff, MWA_BANK2 ),        /* RAM */
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	
	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),
		new MemoryReadAddress( 0xd000, 0xd7ff, MRA_RAM ),
		new MemoryReadAddress( 0xf001, 0xf001, YM2151_status_port_0_r ),
		new MemoryReadAddress( 0xf002, 0xf002, OKIM6295_status_0_r ),
		new MemoryReadAddress( 0xf008, 0xf008, soundlatch_r ),
		new MemoryReadAddress( 0xf00a, 0xf00a, cps1_snd_fade_timer_r ), /* Sound timer fade */
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new MemoryWriteAddress( 0xd000, 0xd7ff, MWA_RAM ),
		new MemoryWriteAddress( 0xf000, 0xf000, YM2151_register_port_0_w ),
		new MemoryWriteAddress( 0xf001, 0xf001, YM2151_data_port_0_w ),
		new MemoryWriteAddress( 0xf002, 0xf002, OKIM6295_data_0_w ),
		new MemoryWriteAddress( 0xf004, 0xf004, cps1_snd_bankswitch_w ),
	//	new MemoryWriteAddress( 0xf006, 0xf006, MWA_NOP ), /* ???? Unknown ???? */
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static MemoryReadAddress qsound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0xbfff, MRA_BANK1 ),  /* banked (contains music data) */
		new MemoryReadAddress( 0xc000, 0xcfff, MRA_RAM ),
		new MemoryReadAddress( 0xd007, 0xd007, qsound_status_r ),
		new MemoryReadAddress( 0xf000, 0xffff, MRA_RAM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress qsound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new MemoryWriteAddress( 0xc000, 0xcfff, MWA_RAM, qsound_sharedram ),
		new MemoryWriteAddress( 0xd000, 0xd000, qsound_data_h_w ),
		new MemoryWriteAddress( 0xd001, 0xd001, qsound_data_l_w ),
		new MemoryWriteAddress( 0xd002, 0xd002, qsound_cmd_w ),
		new MemoryWriteAddress( 0xd003, 0xd003, qsound_banksw_w ),
		new MemoryWriteAddress( 0xf000, 0xffff, MWA_RAM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	
	
	
	static InputPortPtr input_ports_sfzch = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON5 | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON5 | IPF_PLAYER2 );
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_SERVICE, "Pause", KEYCODE_F1, IP_JOY_NONE );/* pause */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE  );/* pause */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON6 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON6 | IPF_PLAYER2  );
	
		PORT_START();       /* DSWA */
		PORT_DIPNAME( 0xff, 0xff, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0xff, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* DSWB */
		PORT_DIPNAME( 0xff, 0xff, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0xff, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* DSWC */
		PORT_DIPNAME( 0xff, 0xff, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0xff, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START();       /* Player 1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER1 );
	
		PORT_START();       /* Player 2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON4 | IPF_PLAYER2 );
	INPUT_PORTS_END(); }}; 
	
	
	
	/********************************************************************
	
				Graphics Layout macros
	
	  These are not really needed, and are used for documentation only.
	
	********************************************************************/
	
	#define SPRITE_LAYOUT(LAYOUT, SPRITES, SPRITE_SEP2, PLANE_SEP) \
	static GfxLayout LAYOUT = new GfxLayout\
	(                                               \
		16,16,   /* 16*16 sprites */             \
		SPRITES,  /* ???? sprites */            \
		4,       /* 4 bits per pixel */            \
		new int[] { PLANE_SEP+8,PLANE_SEP,8,0 },            \
		new int[] { SPRITE_SEP2+0,SPRITE_SEP2+1,SPRITE_SEP2+2,SPRITE_SEP2+3, \
		  SPRITE_SEP2+4,SPRITE_SEP2+5,SPRITE_SEP2+6,SPRITE_SEP2+7,  \
		  0,1,2,3,4,5,6,7, },\
		new int[] { 0*8, 2*8, 4*8, 6*8, 8*8, 10*8, 12*8, 14*8, \
		   16*8, 18*8, 20*8, 22*8, 24*8, 26*8, 28*8, 30*8, }, \
		32*8    /* every sprite takes 32*8*2 consecutive bytes */ \
	);
	
	#define CHAR_LAYOUT(LAYOUT, CHARS, PLANE_SEP) \
	static GfxLayout LAYOUT = new GfxLayout\
	(                                        \
		8,8,    /* 8*8 chars */             \
		CHARS,  /* ???? chars */        \
		4,       /* 4 bits per pixel */     \
		new int[] { PLANE_SEP+8,PLANE_SEP,8,0 },     \
		new int[] { 0,1,2,3,4,5,6,7, },                         \
		new int[] { 0*8, 2*8, 4*8, 6*8, 8*8, 10*8, 12*8, 14*8,}, \
		16*8    /* every sprite takes 32*8*2 consecutive bytes */\
	);
	
	#define TILE32_LAYOUT(LAYOUT, TILES, SEP, PLANE_SEP) \
	static GfxLayout LAYOUT = new GfxLayout\
	(                                                                  \
		32,32,   /* 32*32 tiles */                                 \
		TILES,   /* ????  tiles */                                 \
		4,       /* 4 bits per pixel */                            \
		new int[] { PLANE_SEP+8,PLANE_SEP,8,0},                                        \
		new int[] {                                                          \
		   SEP+0,SEP+1,SEP+2,SEP+3, SEP+4,SEP+5,SEP+6,SEP+7,       \
		   0,1,2,3,4,5,6,7,                                        \
		   16+SEP+0,16+SEP+1,16+SEP+2,                             \
		   16+SEP+3,16+SEP+4,16+SEP+5,                             \
		   16+SEP+6,16+SEP+7,                                      \
		   16+0,16+1,16+2,16+3,16+4,16+5,16+6,16+7                 \
		},                                                         \
		new int[] {                                                          \
		   0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,         \
		   8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32,   \
		   16*32, 17*32, 18*32, 19*32, 20*32, 21*32, 22*32, 23*32, \
		   24*32, 25*32, 26*32, 27*32, 28*32, 29*32, 30*32, 31*32  \
		},                                                         \
		4*32*8    /* every sprite takes 32*8*4 consecutive bytes */\
	);
	
	/* Generic layout, no longer needed, but very useful for testing
	   Will need to change this constant to reflect the gfx region size
	   for the game.
	   Also change the number of characters
	 */
	#define CPS1_ROM_SIZE 0x00000
	#define CPS1_CHARS (CPS1_ROM_SIZE/32)
	CHAR_LAYOUT(cps1_charlayout,     CPS1_CHARS, CPS1_ROM_SIZE/4*16)
	SPRITE_LAYOUT(cps1_spritelayout, CPS1_CHARS/4, CPS1_ROM_SIZE/4*8, CPS1_ROM_SIZE/4*16)
	SPRITE_LAYOUT(cps1_tilelayout,   CPS1_CHARS/4, CPS1_ROM_SIZE/4*8, CPS1_ROM_SIZE/4*16)
	TILE32_LAYOUT(cps1_tilelayout32, CPS1_CHARS/16, CPS1_ROM_SIZE/4*8, CPS1_ROM_SIZE/4*16)
	
	static GfxDecodeInfo cps1_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, cps1_charlayout,    32*16,             32 ),
		new GfxDecodeInfo( REGION_GFX1, 0, cps1_spritelayout,  0,                 32 ),
		new GfxDecodeInfo( REGION_GFX1, 0, cps1_tilelayout,    32*16+32*16,       32 ),
		new GfxDecodeInfo( REGION_GFX1, 0, cps1_tilelayout32,  32*16+32*16+32*16, 32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static void cps1_irq_handler_mus(int irq)
	{
		cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	static YM2151interface ym2151_interface = new YM2151interface
	(
		1,  /* 1 chip */
		3579580,    /* 3.579580 MHz ? */
		new int[] { YM3012_VOL(40,MIXER_PAN_LEFT,40,MIXER_PAN_RIGHT) },
		new WriteYmHandlerPtr[] { cps1_irq_handler_mus }
	);
	
	
	static OKIM6295interface okim6295_interface_7576 = new OKIM6295interface
	(
		1,  /* 1 chip */
		new int[] { 7576 },
		new int[] { REGION_SOUND1 },
		new int[] { 25 }
	);
	
	
	
	static MachineDriver machine_driver_sfzch = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				10000000,
				cps1_readmem,cps1_writemem,null,null,
				cps1_interrupt, 1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				4000000,  /* 4 Mhz ??? TODO: find real FRQ */
				sound_readmem,sound_writemem,null,null,
				ignore_interrupt,0
			)
		},
	    60, 3000,
		1,
		null,
		null,
	
		/* video hardware */
		0x30*8+32*2, 0x1c*8+32*3, new rectangle( 32, 32+0x30*8-1, 32+16, 32+16+0x1c*8-1 ),
	
		cps1_gfxdecodeinfo,
		32*16+32*16+32*16+32*16,   /* lotsa colours */
		32*16+32*16+32*16+32*16,   /* Colour table length */
		null,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
		cps1_eof_callback,
		cps1_vh_start,
		cps1_vh_stop,
		cps1_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] { new MachineSound( SOUND_YM2151,  ym2151_interface ),
		  new MachineSound( SOUND_OKIM6295,  okim6295_interface_7576 )
		},
		0
	);
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	#define CODE_SIZE 0x200000
	
	
	static RomLoadPtr rom_sfzch = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( CODE_SIZE, REGION_CPU1 );     /* 68000 code */
		ROM_LOAD_WIDE_SWAP( "sfzch23",        0x000000, 0x80000, 0x1140743f );
		ROM_LOAD_WIDE_SWAP( "sfza22",         0x080000, 0x80000, 0x8d9b2480 );
		ROM_LOAD_WIDE_SWAP( "sfzch21",        0x100000, 0x80000, 0x5435225d );
		ROM_LOAD_WIDE_SWAP( "sfza20",         0x180000, 0x80000, 0x806e8f38 );
	
		ROM_REGION( 0x800000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "sfz03",         0x000000, 0x80000, 0x9584ac85 );
		ROM_LOAD( "sfz07",         0x080000, 0x80000, 0xbb2c734d );
		ROM_LOAD( "sfz12",         0x100000, 0x80000, 0xf122693a );
		ROM_LOAD( "sfz16",         0x180000, 0x80000, 0x19a5abd6 );
		ROM_LOAD( "sfz01",         0x200000, 0x80000, 0x0dd53e62 );
		ROM_LOAD( "sfz05",         0x280000, 0x80000, 0x2b47b645 );
		ROM_LOAD( "sfz10",         0x300000, 0x80000, 0x2a7d675e );
		ROM_LOAD( "sfz14",         0x380000, 0x80000, 0x09038c81 );
		ROM_LOAD( "sfz04",         0x400000, 0x80000, 0xb983624c );
		ROM_LOAD( "sfz08",         0x480000, 0x80000, 0x454f7868 );
		ROM_LOAD( "sfz13",         0x500000, 0x80000, 0x7cf942c8 );
		ROM_LOAD( "sfz17",         0x580000, 0x80000, 0x248b3b73 );
		ROM_LOAD( "sfz02",         0x600000, 0x80000, 0x94c31e3f );
		ROM_LOAD( "sfz06",         0x680000, 0x80000, 0x74fd9fb1 );
		ROM_LOAD( "sfz11",         0x700000, 0x80000, 0xe35546c8 );
		ROM_LOAD( "sfz15",         0x780000, 0x80000, 0x1aa17391 );
	
		ROM_REGION( 0x18000, REGION_CPU2 );/* 64k for the audio CPU (+banks) */
		ROM_LOAD( "sfz09",         0x00000, 0x08000, 0xc772628b );
		ROM_CONTINUE(              0x10000, 0x08000 );
	
		ROM_REGION( 0x40000, REGION_SOUND1 );/* Samples */
		ROM_LOAD( "sfz18",         0x00000, 0x20000, 0x61022b2d );
		ROM_LOAD( "sfz19",         0x20000, 0x20000, 0x3b5886d5 );
	ROM_END(); }}; 
	
	
	
	static const struct IODevice io_sfzch[] = {
	
	    { IO_END }
	};
	
	
	CONS( 1995, sfzch,    0,        sfzch,     sfzch,    0,        "Capcom", "CPS Changer (Street Fighter ZERO)" )
	
	}
