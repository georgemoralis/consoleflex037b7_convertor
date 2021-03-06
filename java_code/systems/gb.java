/***************************************************************************

  gb.c

  Driver file to handle emulation of the Nintendo Gameboy.
  By:

  Hans de Goede               1998

  Todo list:
  Done entries kept for historical reasons, besides that it's nice to see
  what is already done instead of what has to be done.

Priority:  Todo:                                                  Done:
  2        Replace Marat's  vidhrdw/gb.c  by Playboy code           *
  2        Clean & speed up vidhrdw/gb.c                            *
  2        Replace Marat's  Z80gb/Z80gb.c by Playboy code           *
  2        Transform Playboys Z80gb.c to big case method            *
  2        Clean up Z80gb.c                                         *
  2        Fix / optimise halt instruction                          *
  2        Do correct lcd stat timing
  2        Generate lcd stat interrupts
  2        Replace Marat's code in machine/gb.c by Playboy code
  1        Check, and fix if needed flags bug which troubles ffa
  1        Save/restore battery backed ram
           (urgent needed to play zelda ;)
  1        Add sound
  0        Add supergb support
  0        Add palette editting, save & restore
  0        Add somekind of backdrop support
  0        Speedups if remotly possible

  2 = has to be done before first public release
  1 = should be added later on
  0 = bells and whistles

***************************************************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class gb
{
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),   /* 16k fixed ROM BANK #0*/
		new MemoryReadAddress( 0x4000, 0x7fff, MRA_BANK1 ), /* 16k switched ROM bank */
		new MemoryReadAddress( 0x8000, 0x9fff, MRA_RAM ),   /* 8k video ram */
		new MemoryReadAddress( 0xa000, 0xbfff, MRA_BANK2 ), /* 8k RAM bank (on cartridge) */
		new MemoryReadAddress( 0xc000, 0xff03, MRA_RAM ),   /* internal ram + echo + sprite Ram  IO */
		new MemoryReadAddress( 0xff04, 0xff04, gb_r_divreg ),    /* special case for the division reg */
		new MemoryReadAddress( 0xff05, 0xff05, gb_r_timer_cnt ), /* special case for the timer count reg */
		new MemoryReadAddress( 0xff06, 0xffff, MRA_RAM ),   /* IO */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),            /* plain rom */
		new MemoryWriteAddress( 0x2000, 0x3fff, gb_rom_bank_select ), /* rom bank select */
		new MemoryWriteAddress( 0x4000, 0x5fff, gb_ram_bank_select ), /* ram bank select */
		new MemoryWriteAddress( 0x6000, 0x7fff, MWA_ROM ),            /* plain rom */
		new MemoryWriteAddress( 0x8000, 0x9fff, MWA_RAM ),            /* plain ram */
		new MemoryWriteAddress( 0xa000, 0xbfff, MWA_BANK2 ),          /* banked (cartridge) ram */
		new MemoryWriteAddress( 0xc000, 0xfeff, MWA_RAM, videoram, videoram_size ), /* video  sprite ram */
		new MemoryWriteAddress( 0xff00, 0xffff, gb_w_io ),	        /* gb io */
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static InputPortPtr input_ports_gameboy = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
	    PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);
	    PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
	    PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP   );
	    PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
	    PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1       );
	    PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2       );
		/*PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN, "select", KEYCODE_LSHIFT, IP_JOY_DEFAULT );*/
		/*PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN, "start",  KEYCODE_Z,      IP_JOY_DEFAULT );*/
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "select", KEYCODE_3, IP_JOY_DEFAULT );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "start",  KEYCODE_1, IP_JOY_DEFAULT );
	INPUT_PORTS_END(); }}; 
	
	static unsigned char palette[] =
	{
		0xFF,0xFF,0xFF, 	   /* Background colours */
		0xB0,0xB0,0xB0,
		0x60,0x60,0x60,
		0x00,0x00,0x00,
	};
	
	static unsigned short colortable[] = {
	    0,1,2,3,    /* Background colours */
	    0,1,2,3,    /* Sprite 0 colours */
	    0,1,2,3,    /* Sprite 1 colours */
	    0,1,2,3,    /* Window colours */
	};
	
	/* Initialise the palette */
	static void gb_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable,const UBytePtr color_prom)
	{
		memcpy(sys_palette,palette,sizeof(palette));
		memcpy(sys_colortable,colortable,sizeof(colortable));
	}
	
	static MachineDriver machine_driver_gameboy = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80GB,
				4194304,	  /* 4.194304 Mhz */
				readmem,writemem,null,null,
				gb_scanline_interrupt, 154 *3 /* 1 int each scanline ! */
			)
		},
		60, 0,	/* frames per second, vblank duration */
		1,
		gb_init_machine,
		NULL,	/* shutdown machine */
	
		/* video hardware (double size) */
		160, 144,
		new rectangle( 0, 160-1, 0, 144-1 ),
		gfxdecodeinfo,
		(sizeof (palette))/sizeof(palette[null])/3,
		sizeof(colortable)/sizeof(colortable[null]),
		gb_init_palette,				/* init palette */
	
		VIDEO_TYPE_RASTER,
		null,
		gb_vh_start,					/* vh_start */
	    gb_vh_stop,                     /* vh_stop */
		gb_vh_screen_refresh,
	
		/* sound hardware */
		0,0,0,0,
	);
	
	static const struct IODevice io_gameboy[] = {
		{
			IO_CARTSLOT,		/* type */
			1,					/* count */
			"gb\0",				/* file extensions */
			IO_RESET_ALL,		/* reset if file changed */
	        gb_id_rom,          /* id */
			gb_load_rom,		/* init */
			NULL,				/* exit */
			NULL,				/* info */
			NULL,               /* open */
			NULL,               /* close */
			NULL,               /* status */
			NULL,               /* seek */
			NULL,				/* tell */
	        NULL,               /* input */
			NULL,               /* output */
			NULL,               /* input_chunk */
			NULL                /* output_chunk */
		},
		{ IO_END }
	};
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	#define rom_gameboy NULL
	
	/*     YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY   FULLNAME */
	CONSX( 1990, gameboy,  0,		 gameboy,  gameboy,  0,		   "Nintendo", "GameBoy", GAME_NOT_WORKING | GAME_NO_SOUND )
	
}
