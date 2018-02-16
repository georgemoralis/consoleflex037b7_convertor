/***************************************************************************

Apple II

This family of computers bank-switches everything up the wazoo.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class apple2
{
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x01ff, MRA_BANK4 ),
		new MemoryReadAddress( 0x0200, 0xbfff, MRA_BANK5 ),
	//	new MemoryReadAddress( 0x0200, 0xbfff, MRA_RAM ),
		new MemoryReadAddress( 0xc000, 0xc00f, apple2_c00x_r ),
		new MemoryReadAddress( 0xc010, 0xc01f, apple2_c01x_r ),
		new MemoryReadAddress( 0xc020, 0xc02f, apple2_c02x_r ),
		new MemoryReadAddress( 0xc030, 0xc03f, apple2_c03x_r ),
		new MemoryReadAddress( 0xc040, 0xc04f, apple2_c04x_r ),
		new MemoryReadAddress( 0xc050, 0xc05f, apple2_c05x_r ),
		new MemoryReadAddress( 0xc060, 0xc06f, apple2_c06x_r ),
		new MemoryReadAddress( 0xc070, 0xc07f, apple2_c07x_r ),
		new MemoryReadAddress( 0xc080, 0xc08f, apple2_c08x_r ),
		new MemoryReadAddress( 0xc090, 0xc09f, apple2_c0xx_slot1_r ),
		new MemoryReadAddress( 0xc0a0, 0xc0af, apple2_c0xx_slot2_r ),
		new MemoryReadAddress( 0xc0b0, 0xc0bf, apple2_c0xx_slot3_r ),
		new MemoryReadAddress( 0xc0c0, 0xc0cf, apple2_c0xx_slot4_r ),
		new MemoryReadAddress( 0xc0d0, 0xc0df, apple2_c0xx_slot5_r ),
		new MemoryReadAddress( 0xc0e0, 0xc0ef, apple2_c0xx_slot6_r ),
		new MemoryReadAddress( 0xc0f0, 0xc0ff, apple2_c0xx_slot7_r ),
		new MemoryReadAddress( 0xc400, 0xc4ff, apple2_slot4_r ),
		new MemoryReadAddress( 0xc100, 0xc7ff, MRA_BANK3 ),
	//	new MemoryReadAddress( 0xc100, 0xc1ff, apple2_slot1_r ),
	//	new MemoryReadAddress( 0xc200, 0xc2ff, apple2_slot2_r ),
	//	new MemoryReadAddress( 0xc300, 0xc3ff, apple2_slot3_r ),
	//	new MemoryReadAddress( 0xc500, 0xc5ff, apple2_slot5_r ),
	//	new MemoryReadAddress( 0xc600, 0xc6ff, apple2_slot6_r ),
	//	new MemoryReadAddress( 0xc700, 0xc7ff, apple2_slot7_r ),
		new MemoryReadAddress( 0xc800, 0xcffe, MRA_BANK6 ),
	//	new MemoryReadAddress( 0xcfff, 0xcfff, apple2_slotrom_disable ),
		new MemoryReadAddress( 0xd000, 0xdfff, MRA_BANK1 ),
		new MemoryReadAddress( 0xe000, 0xffff, MRA_BANK2 ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x01ff, MWA_BANK4 ),
		new MemoryWriteAddress( 0x0200, 0xbfff, MWA_BANK5 ),
	//	new MemoryWriteAddress( 0x0200, 0x03ff, MWA_RAM ),
	//	new MemoryWriteAddress( 0x0400, 0x07ff, apple2_lores_text1_w, apple2_lores_text1_ram ),
	//	new MemoryWriteAddress( 0x0800, 0x0bff, apple2_lores_text2_w, apple2_lores_text2_ram ),
	//	new MemoryWriteAddress( 0x0c00, 0x1fff, MWA_RAM ),
	//	new MemoryWriteAddress( 0x2000, 0x3fff, apple2_hires1_w, apple2_hires1_ram ),
	//	new MemoryWriteAddress( 0x4000, 0x5fff, apple2_hires2_w, apple2_hires2_ram ),
	//	new MemoryWriteAddress( 0x6000, 0xbfff, MWA_RAM ),
		new MemoryWriteAddress( 0xc000, 0xc00f, apple2_c00x_w ),
		new MemoryWriteAddress( 0xc010, 0xc01f, apple2_c01x_w ),
		new MemoryWriteAddress( 0xc020, 0xc02f, apple2_c02x_w ),
		new MemoryWriteAddress( 0xc030, 0xc03f, apple2_c03x_w ),
	//	new MemoryWriteAddress( 0xc040, 0xc04f, apple2_c04x_w ),
		new MemoryWriteAddress( 0xc050, 0xc05f, apple2_c05x_w ),
	//	new MemoryWriteAddress( 0xc060, 0xc06f, apple2_c06x_w ),
	//	new MemoryWriteAddress( 0xc070, 0xc07f, apple2_c07x_w ),
		new MemoryWriteAddress( 0xc080, 0xc08f, apple2_c08x_w ),
		new MemoryWriteAddress( 0xc090, 0xc09f, apple2_c0xx_slot1_w ),
		new MemoryWriteAddress( 0xc0a0, 0xc0af, apple2_c0xx_slot2_w ),
		new MemoryWriteAddress( 0xc0b0, 0xc0bf, apple2_c0xx_slot3_w ),
		new MemoryWriteAddress( 0xc0c0, 0xc0cf, apple2_c0xx_slot4_w ),
		new MemoryWriteAddress( 0xc0d0, 0xc0df, apple2_c0xx_slot5_w ),
		new MemoryWriteAddress( 0xc0e0, 0xc0ef, apple2_c0xx_slot6_w ),
		new MemoryWriteAddress( 0xc0f0, 0xc0ff, apple2_c0xx_slot7_w ),
		new MemoryWriteAddress( 0xc100, 0xc1ff, apple2_slot1_w, apple2_slot1 ),
		new MemoryWriteAddress( 0xc200, 0xc2ff, apple2_slot2_w, apple2_slot2 ),
		new MemoryWriteAddress( 0xc300, 0xc3ff, apple2_slot3_w, apple2_slot3 ),
		new MemoryWriteAddress( 0xc400, 0xc4ff, apple2_slot4_w, apple2_slot4 ),
		new MemoryWriteAddress( 0xc500, 0xc5ff, apple2_slot5_w, apple2_slot5 ),
		new MemoryWriteAddress( 0xc600, 0xc6ff, apple2_slot6_w, apple2_slot6 ),
		new MemoryWriteAddress( 0xc700, 0xc7ff, apple2_slot7_w, apple2_slot7 ),
		new MemoryWriteAddress( 0xc100, 0xc7ff, MWA_BANK3, apple2_slot_rom ), /* Just here to initialize the pointer */
		new MemoryWriteAddress( 0xd000, 0xdfff, MWA_BANK1 ),
		new MemoryWriteAddress( 0xe000, 0xffff, MWA_BANK2 ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	/**************************************************************************
	***************************************************************************/
	
	static InputPortPtr input_ports_apple2 = new InputPortPtr(){ public void handler() { 
	
		PORT_START();  /* VBLANK */
		PORT_BIT ( 0xBF, IP_ACTIVE_HIGH, IPT_UNUSED );
		PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_VBLANK );
	
		PORT_START();  /* KEYS #1 */
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Backsp ", KEYCODE_BACKSPACE,   IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Left   ", KEYCODE_LEFT,        IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Tab    ", KEYCODE_TAB,         IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Down   ", KEYCODE_DOWN,        IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Up     ", KEYCODE_UP,          IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Enter  ", KEYCODE_ENTER,       IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Right  ", KEYCODE_RIGHT,       IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Escape ", KEYCODE_ESC,         IP_JOY_NONE );
	
		PORT_START();  /* KEYS #2 */
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Space  ", KEYCODE_SPACE,       IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "'      ", KEYCODE_QUOTE,       IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, ",      ", KEYCODE_COMMA,       IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "-      ", KEYCODE_MINUS,       IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_KEYBOARD, ".      ", KEYCODE_STOP,        IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "/ ?    ", KEYCODE_SLASH,       IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_HIGH, IPT_KEYBOARD, "0      ", KEYCODE_0,           IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_HIGH, IPT_KEYBOARD, "1      ", KEYCODE_1,           IP_JOY_NONE );
	
		PORT_START();  /* KEYS #3 */
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, "2      ", KEYCODE_2,           IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "3      ", KEYCODE_3,           IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "4      ", KEYCODE_4,           IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "5      ", KEYCODE_5,           IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_KEYBOARD, "6      ", KEYCODE_6,           IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "7      ", KEYCODE_7,           IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_HIGH, IPT_KEYBOARD, "8      ", KEYCODE_8,           IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_HIGH, IPT_KEYBOARD, "9      ", KEYCODE_9,           IP_JOY_NONE );
	
		PORT_START();  /* KEYS #4 */
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, ":      ", KEYCODE_COLON,       IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "=      ", KEYCODE_EQUALS,      IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "[ {    ", KEYCODE_OPENBRACE,   IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "\\ |    ", KEYCODE_BACKSLASH,   IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_KEYBOARD, "] }    ", KEYCODE_CLOSEBRACE,  IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "^ ~    ", KEYCODE_TILDE,       IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_HIGH, IPT_KEYBOARD, "a A    ", KEYCODE_A,           IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_HIGH, IPT_KEYBOARD, "b B    ", KEYCODE_B,           IP_JOY_NONE );
	
		PORT_START();  /* KEYS #5 */
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, "c C    ", KEYCODE_C,           IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "d D    ", KEYCODE_D,           IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "e E    ", KEYCODE_E,           IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "f F    ", KEYCODE_F,           IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_KEYBOARD, "g G    ", KEYCODE_G,           IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "h H    ", KEYCODE_H,           IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_HIGH, IPT_KEYBOARD, "i I    ", KEYCODE_I,           IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_HIGH, IPT_KEYBOARD, "j J    ", KEYCODE_J,           IP_JOY_NONE );
	
		PORT_START();  /* KEYS #6 */
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, "k K    ", KEYCODE_K,           IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "l L    ", KEYCODE_L,           IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "m M    ", KEYCODE_M,           IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "n N    ", KEYCODE_N,           IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_KEYBOARD, "o O    ", KEYCODE_O,           IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "p P    ", KEYCODE_P,           IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_HIGH, IPT_KEYBOARD, "q Q    ", KEYCODE_Q,           IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_HIGH, IPT_KEYBOARD, "r R    ", KEYCODE_R,           IP_JOY_NONE );
	
		PORT_START();  /* KEYS #7 */
		PORT_BITX( 0x01, IP_ACTIVE_HIGH, IPT_KEYBOARD, "s S    ", KEYCODE_S,           IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "t T    ", KEYCODE_T,           IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "u U    ", KEYCODE_U,           IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "v V    ", KEYCODE_V,           IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_KEYBOARD, "w W    ", KEYCODE_W,           IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "x X    ", KEYCODE_X,           IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_HIGH, IPT_KEYBOARD, "y Y    ", KEYCODE_Y,           IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_HIGH, IPT_KEYBOARD, "z Z    ", KEYCODE_Z,           IP_JOY_NONE );
	
	    PORT_START();  /* Special keys */
		PORT_BITX( 0x01, IP_ACTIVE_LOW, IPT_KEYBOARD | IPF_TOGGLE, "Caps Lock", KEYCODE_CAPSLOCK, IP_JOY_DEFAULT );
		PORT_BITX( 0x02, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Left Shift",            KEYCODE_LSHIFT,   IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Right Shift",           KEYCODE_RSHIFT,   IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Left Control",          KEYCODE_LCONTROL, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON3,  "Button 3",              IP_KEY_DEFAULT,   IP_JOY_DEFAULT );
		PORT_BITX( 0x20, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Reset",                 KEYCODE_F3,       IP_JOY_NONE );
	
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout apple2_text_layout = new GfxLayout
	(
		14,8,		   /* 14*8 characters */
		256,		   /* 256 characters */
		1,			   /* 1 bits per pixel */
		new int[] { 0 },		   /* no bitplanes; 1 bit per pixel */
		new int[] { 7, 7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1 },	/* x offsets */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8		   /* every char takes 8 bytes */
	);
	
	static GfxLayout apple2_dbl_text_layout = new GfxLayout
	(
		7,8,		   /* 7*8 characters */
		256,		   /* 256 characters */
		1,			   /* 1 bits per pixel */
		new int[] { 0 },		   /* no bitplanes; 1 bit per pixel */
		new int[] { 7, 6, 5, 4, 3, 2, 1 },	/* x offsets */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8		   /* every char takes 8 bytes */
	);
	
	static GfxLayout apple2_lores_layout = new GfxLayout
	(
		14,8,		   /* 14*8 characters */
		0x100,		   /* 0x100 characters */
		1,			   /* 1 bits per pixel */
		new int[] { 0 },		   /* no bitplanes; 1 bit per pixel */
		new int[] { 7+16, 6+16, 5+16, 4+16, 3+16, 2+16, 1+16,
		  0+16, 7+16, 6+16, 5+16, 4+16, 3+16, 2+16 },	/* x offsets */
		new int[] { 0, 0, 0, 0, 4*8, 4*8, 4*8, 4*8 },
		8*8		   /* every char takes 8 bytes */
	);
	
	static GfxLayout apple2_dbl_lores_layout = new GfxLayout
	(
		7,8,		   /* 7*16 characters */
		0x100,		   /* 0x100 characters */
		1,			   /* 1 bits per pixel */
		new int[] { 0 },		   /* no bitplanes; 1 bit per pixel */
		new int[] { 7+16, 6+16, 5+16, 4+16, 3+16, 2+16, 1+16 },	/* x offsets */
		new int[] { 0, 0, 0, 0, 4*8, 4*8, 4*8, 4*8 },
	    8*8        /* every char takes 8 bytes */
	);
	
	static GfxLayout apple2_hires_layout = new GfxLayout
	(
		14,1,		   /* 14*1 characters */
		0x80,		   /* 0x80 characters */
		1,			   /* 1 bits per pixel */
		new int[] { 0 },		   /* no bitplanes; 1 bit per pixel */
		new int[] { 7, 7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1 },	/* x offsets */
		new int[] { 0 },
		8*8		   /* every char takes 1 byte */
	);
	
	static GfxLayout apple2_hires_shifted_layout = new GfxLayout
	(
		14,1,		   /* 14*1 characters */
		0x80,		   /* 0x80 characters */
		1,			   /* 1 bits per pixel */
		new int[] { 0 },		   /* no bitplanes; 1 bit per pixel */
		new int[] { 0, 7, 7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1 },	/* x offsets */
		new int[] { 0 },
		8*8		   /* every char takes 1 byte */
	);
	
	static GfxLayout apple2_dbl_hires_layout = new GfxLayout
	(
		7,1,		   /* 7*2 characters */
		0x800,		   /* 0x800 characters */
		1,			   /* 1 bits per pixel */
		new int[] { 0 },		   /* no bitplanes; 1 bit per pixel */
		new int[] { 7, 6, 5, 4, 3, 2, 1 },	/* x offsets */
		new int[] { 0 },
		8*1		   /* every char takes 1 byte */
	);
	
	static GfxDecodeInfo apple2_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, apple2_text_layout, 0, 16 ),
		new GfxDecodeInfo( 1, 0x0000, apple2_dbl_text_layout, 0, 16 ),
		new GfxDecodeInfo( 1, 0x0800, apple2_lores_layout, 0, 16 ),		/* Even characters */
		new GfxDecodeInfo( 1, 0x0801, apple2_lores_layout, 0, 16 ),		/* Odd characters */
		new GfxDecodeInfo( 1, 0x0800, apple2_dbl_lores_layout, 0, 16 ),
		new GfxDecodeInfo( 1, 0x0800, apple2_hires_layout, 0, 16 ),
		new GfxDecodeInfo( 1, 0x0C00, apple2_hires_shifted_layout, 0, 16 ),
		new GfxDecodeInfo( 1, 0x0800, apple2_dbl_hires_layout, 0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static unsigned char palette[] =
	{
		0x00, 0x00, 0x00, /* Black */
		0xD0, 0x00, 0x30, /* Dark Red */
		0x00, 0x00, 0x90, /* Dark Blue */
		0xD0, 0x20, 0xD0, /* Purple */
		0x00, 0x70, 0x20, /* Dark Green */
		0x50, 0x50, 0x50, /* Dark Grey */
		0x20, 0x20, 0xF0, /* Medium Blue */
		0x60, 0xA0, 0xF0, /* Light Blue */
		0x80, 0x50, 0x00, /* Brown */
		0xF0, 0x60, 0x00, /* Orange */
		0xA0, 0xA0, 0xA0, /* Light Grey */
		0xF0, 0x90, 0x80, /* Pink */
		0x10, 0xD0, 0x00, /* Light Green */
		0xF0, 0xF0, 0x00, /* Yellow */
		0x40, 0xF0, 0x90, /* Aquamarine */
		0xF0, 0xF0, 0xF0, /* White */
	};
	
	static unsigned short colortable[] =
	{
		0,0,
		1,0,
		2,0,
		3,0,
		4,0,
		5,0,
		6,0,
		7,0,
		8,0,
		9,0,
		10,0,
		11,0,
		12,0,
		13,0,
		14,0,
		15,0,
	};
	
	
	/* Initialise the palette */
	static void apple_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable,const UBytePtr color_prom)
	{
		memcpy(sys_palette,palette,sizeof(palette));
		memcpy(sys_colortable,colortable,sizeof(colortable));
	}
	
	
	static DACinterface apple2_DAC_interface = new DACinterface
	(
		1,			/* number of DACs */
		new int[] { 100 }		/* volume */
	);
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
		1022727,	/* 1.023 MHz */
		new int[] { 100, 100 },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 }
	);
	
	static MachineDriver machine_driver_standard = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6502,
				1022727,			/* 1.023 Mhz */
				readmem,writemem,
				null,null,				/* no readport, writeport for 6502 */
				apple2_interrupt,1,
			),
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION,		/* frames per second, vblank duration */
		1,
		apple2e_init_machine,
		null,
	
		/* video hardware */
		280*2,							/* screen width */
		192,							/* screen height */
		new rectangle( 0, (280*2)-1,0,192-1),		/* visible_area */
		apple2_gfxdecodeinfo,			/* graphics decode info */
		sizeof(palette)/3,							/* 2 colors used for the characters */
		sizeof(colortable)/sizeof(unsigned short),	/* 2 colors used for the characters */
		apple_init_palette,							/* init palette */
	
		VIDEO_TYPE_RASTER,
		null,
		apple2_vh_start,
		apple2_vh_stop,
		apple2_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_DAC,
				apple2_DAC_interface
			),
			new MachineSound(
				SOUND_AY8910,
				ay8910_interface
			)
		}
	);
	
	static MachineDriver machine_driver_enhanced = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M65C02,
				1022727,			/* 1.023 Mhz */
				readmem,writemem,
				null,null,				/* no readport, writeport for 6502 */
				apple2_interrupt,1,
			),
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION,		/* frames per second, vblank duration */
		1,
		apple2e_init_machine,
		null,
	
		/* video hardware */
		280*2,							/* screen width */
		192,							/* screen height */
		new rectangle( 0, (280*2)-1,0,192-1),		/* visible_area */
		apple2_gfxdecodeinfo,			/* graphics decode info */
		sizeof(palette)/3,							/* 2 colors used for the characters */
		sizeof(colortable)/sizeof(unsigned short),	/* 2 colors used for the characters */
		apple_init_palette,							/* init palette */
	
		VIDEO_TYPE_RASTER,
		null,
		apple2_vh_start,
		apple2_vh_stop,
		apple2_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_DAC,
				apple2_DAC_interface
			),
			new MachineSound(
				SOUND_AY8910,
				ay8910_interface
			)
		}
	);
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_apple2e = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x24700,REGION_CPU1);
		/* 64k main RAM, 64k aux RAM */
		ROM_LOAD ( "a2e.cd", 0x20000, 0x2000, 0xe248835e );
		ROM_LOAD ( "a2e.ef", 0x22000, 0x2000, 0xfc3d59d8 );
		/* 0x700 for individual slot ROMs */
		ROM_LOAD ( "disk2_33.rom", 0x24500, 0x0100, 0xce7144f6 );/* Disk II ROM - DOS 3.3 version */
	
		ROM_REGION(0x2000,REGION_GFX1);
		ROM_LOAD ( "a2e.vid", 0x0000, 0x1000, 0x816a86f1 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_apple2ee = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x24700,REGION_CPU1);
		ROM_LOAD ( "a2ee.cd", 0x20000, 0x2000, 0x443aa7c4 );
		ROM_LOAD ( "a2ee.ef", 0x22000, 0x2000, 0x95e10034 );
		/* 0x4000 for bankswitched RAM */
		/* 0x700 for individual slot ROMs */
		ROM_LOAD ( "disk2_33.rom", 0x24500, 0x0100, 0xce7144f6 );/* Disk II ROM - DOS 3.3 version */
	
		ROM_REGION(0x2000,REGION_GFX1);
		ROM_LOAD ( "a2ee.vid", 0x0000, 0x1000, 0x2651014d);
	ROM_END(); }}; 
	
	static RomLoadPtr rom_apple2ep = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x24700,REGION_CPU1);
		ROM_LOAD ("a2ept.cf", 0x20000, 0x4000, 0x02b648c8);
		/* 0x4000 for bankswitched RAM */
		/* 0x700 for individual slot ROMs */
		ROM_LOAD ("disk2_33.rom", 0x24500, 0x0100, 0xce7144f6);/* Disk II ROM - DOS 3.3 version */
	
		ROM_REGION(0x2000,REGION_GFX1);
		ROM_LOAD("a2ept.vid", 0x0000, 0x1000, 0x2651014d);
	ROM_END(); }}; 
	
	static RomLoadPtr rom_apple2c = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x24700,REGION_CPU1);
		ROM_LOAD ( "a2c.128", 0x20000, 0x4000, 0xf0edaa1b );
	
		ROM_REGION(0x2000,REGION_GFX1);
		ROM_LOAD ( "a2c.vid", 0x0000, 0x1000, 0x2651014d );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_apple2c0 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x28000,REGION_CPU1);
		ROM_LOAD("a2c.256", 0x20000, 0x8000, 0xc8b979b3);
	
		ROM_REGION(0x2000,REGION_GFX1);
		ROM_LOAD("a2c.vid", 0x0000, 0x1000, 0x2651014d);
	ROM_END(); }}; 
	
	static RomLoadPtr rom_apple2cp = new RomLoadPtr(){ public void handler(){ 
	    ROM_REGION(0x28000,REGION_CPU1);
	    ROM_LOAD("a2cplus.mon", 0x20000, 0x8000, 0x0b996420);
	
	    ROM_REGION(0x2000,REGION_GFX1);
	    ROM_LOAD("a2cplus.vid", 0x0000, 0x1000, 0x2651014d);
	ROM_END(); }}; 
	
	static const struct IODevice io_apple2[] =
	{
		{
			IO_FLOPPY,			/* type */
			2,					/* count */
			"dsk\0",            /* file extensions */
			IO_RESET_NONE,		/* reset if file changed */
	        NULL,               /* id */
			apple2_floppy_init, /* init */
			NULL,				/* exit */
	        NULL,               /* info */
	        NULL,               /* open */
	        NULL,               /* close */
	        NULL,               /* status */
	        NULL,               /* seek */
	        NULL,               /* input */
	        NULL,               /* output */
	        NULL,               /* input_chunk */
	        NULL                /* output_chunk */
	    },
	    { IO_END }
	};
	
	#define io_apple2c	io_apple2
	#define io_apple2c0 io_apple2
	#define io_apple2cp io_apple2
	#define io_apple2e	io_apple2
	#define io_apple2ee io_apple2
	#define io_apple2ep io_apple2
	
	/*     YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY            FULLNAME */
	COMP ( 1983, apple2e,  0,		 standard, apple2,	 0, 	   "Apple Computer", "Apple //e" )
	COMP ( 1985, apple2ee, apple2e,  enhanced, apple2,	 0, 	   "Apple Computer", "Apple //e (enhanced)" )
	COMP ( 1987, apple2ep, apple2e,  enhanced, apple2,	 0, 	   "Apple Computer", "Apple //e (Platinum)" )
	COMP ( 1984, apple2c,  0,		 enhanced, apple2,	 0, 	   "Apple Computer", "Apple //c" )
	COMP ( 1986, apple2c0, apple2c,  enhanced, apple2,	 0, 	   "Apple Computer", "Apple //c (3.5 ROM)" )
	COMP ( 1988, apple2cp, apple2c,  enhanced, apple2,	 0, 	   "Apple Computer", "Apple //c Plus" )
	
}
