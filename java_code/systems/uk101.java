/**********************************************************************
UK101 Memory map

	CPU: 6502 @ 1.0Mhz

Interrupts:	None.

Video:		Memory mapped

Sound:		None

Hardware:	MC6850

		0000-0FFF	RAM (standard)
		1000-1FFF	RAM (expanded)
		2000-9FFF	RAM	(emulator only)
		A000-BFFF	ROM (basic)
		C000-CFFF	NOP
		D000-D3FF	RAM (video)
		D400-DEFF	NOP
		DF00-DF00	H/W (Keyboard)
		DF01-EFFF	NOP
		F000-F001	H/W (MC6850)
		F002-F7FF	NOP
		F800-FFFF	ROM (monitor)
**********************************************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class uk101
{
	
	/* memory w/r functions */
	
	static MemoryReadAddress uk101_readmem[] =
	{
		new MemoryReadAddress(0x0000, 0x0fff, MRA_RAM),
		new MemoryReadAddress(0x1000, 0x1fff, MRA_RAM),
		new MemoryReadAddress(0x2000, 0x9fff, MRA_RAM),
		new MemoryReadAddress(0xa000, 0xbfff, MRA_ROM),
		new MemoryReadAddress(0xc000, 0xcfff, MRA_NOP),
		new MemoryReadAddress(0xd000, 0xd3ff, videoram_r),
		new MemoryReadAddress(0xd400, 0xdeff, MRA_NOP),
		new MemoryReadAddress(0xdf00, 0xdf00, uk101_keyb_r),
		new MemoryReadAddress(0xdf01, 0xefff, MRA_NOP),
		new MemoryReadAddress(0xf000, 0xf001, acia6850_0_r),
		new MemoryReadAddress(0xf002, 0xf7ff, MRA_NOP),
		new MemoryReadAddress(0xf800, 0xffff, MRA_ROM),
		new MemoryReadAddress(-1)
	};
	
	static MemoryWriteAddress uk101_writemem[] =
	{
		new MemoryWriteAddress(0x0000, 0x0fff, MWA_RAM),
		new MemoryWriteAddress(0x1000, 0x1fff, MWA_RAM),
		new MemoryWriteAddress(0x2000, 0x9fff, MWA_RAM),
		new MemoryWriteAddress(0xa000, 0xbfff, MWA_ROM),
		new MemoryWriteAddress(0xc000, 0xcfff, MWA_NOP),
		new MemoryWriteAddress(0xd000, 0xd3ff, videoram_w, videoram, videoram_size),
		new MemoryWriteAddress(0xd400, 0xdeff, MWA_NOP),
		new MemoryWriteAddress(0xdf00, 0xdf00, uk101_keyb_w),
		new MemoryWriteAddress(0xdf01, 0xefff, MWA_NOP),
		new MemoryWriteAddress(0xf000, 0xf001, acia6850_0_w),
		new MemoryWriteAddress(0xf002, 0xf7ff, MWA_NOP),
		new MemoryWriteAddress(0xf800, 0xffff, MWA_ROM),
		new MemoryWriteAddress(-1)
	};
	
	static MemoryReadAddress superbrd_readmem[] =
	{
		new MemoryReadAddress(0x0000, 0x0fff, MRA_RAM),
		new MemoryReadAddress(0x1000, 0x1fff, MRA_RAM),
		new MemoryReadAddress(0x2000, 0x9fff, MRA_RAM),
		new MemoryReadAddress(0xa000, 0xbfff, MRA_ROM),
		new MemoryReadAddress(0xc000, 0xcfff, MRA_NOP),
		new MemoryReadAddress(0xd000, 0xd7ff, videoram_r),
		new MemoryReadAddress(0xd800, 0xdeff, MRA_NOP),
		new MemoryReadAddress(0xdf00, 0xdf00, uk101_keyb_r),
		new MemoryReadAddress(0xdf01, 0xefff, MRA_NOP),
		new MemoryReadAddress(0xf000, 0xf001, acia6850_0_r),
		new MemoryReadAddress(0xf002, 0xf7ff, MRA_NOP),
		new MemoryReadAddress(0xf800, 0xffff, MRA_ROM),
		new MemoryReadAddress(-1)
	};
	
	static MemoryWriteAddress superbrd_writemem[] =
	{
		new MemoryWriteAddress(0x0000, 0x0fff, MWA_RAM),
		new MemoryWriteAddress(0x1000, 0x1fff, MWA_RAM),
		new MemoryWriteAddress(0x2000, 0x9fff, MWA_RAM),
		new MemoryWriteAddress(0xa000, 0xbfff, MWA_ROM),
		new MemoryWriteAddress(0xc000, 0xcfff, MWA_NOP),
		new MemoryWriteAddress(0xd000, 0xd7ff, videoram_w, videoram, videoram_size),
		new MemoryWriteAddress(0xd800, 0xdeff, MWA_NOP),
		new MemoryWriteAddress(0xdf00, 0xdf00, uk101_keyb_w),
		new MemoryWriteAddress(0xdf01, 0xefff, MWA_NOP),
		new MemoryWriteAddress(0xf000, 0xf001, acia6850_0_w),
		new MemoryWriteAddress(0xf002, 0xf7ff, MWA_NOP),
		new MemoryWriteAddress(0xf800, 0xffff, MWA_ROM),
		new MemoryWriteAddress(-1)
	};
	
	/* graphics output */
	
	static GfxLayout uk101_charlayout = new GfxLayout
	(
		8, 16,
		256,
		1,
		new int[] { 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 0*8, 1*8, 1*8, 2*8, 2*8, 3*8, 3*8,
		  4*8, 4*8, 5*8, 5*8, 6*8, 6*8, 7*8, 7*8 },
		8 * 8
	);
	
	static GfxDecodeInfo uk101_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, uk101_charlayout, 0, 1),
		new GfxDecodeInfo( -1 )
	};
	
	static unsigned char uk101_palette[2 * 3] =
	{
		0x00, 0x00, 0x00,	/* Black */
		0xff, 0xff, 0xff	/* White */
	};
	
	static unsigned short uk101_colortable[] =
	{
		0,1
	};
	
	static void uk101_init_palette (UBytePtr sys_palette,
						unsigned short *sys_colortable,
						const UBytePtr color_prom)
	{
		memcpy (sys_palette, uk101_palette, sizeof (uk101_palette));
		memcpy (sys_colortable, uk101_colortable, sizeof (uk101_colortable));
	}
	
	/* keyboard input */
	
	static InputPortPtr input_ports_uk101 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* 0: DF00 & 0x80 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "7", KEYCODE_7, IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "6", KEYCODE_6, IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "5", KEYCODE_5, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "4", KEYCODE_4, IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "3", KEYCODE_3, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "2", KEYCODE_2, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "1", KEYCODE_1, IP_JOY_NONE );
		PORT_START();  /* 1: DF00 & 0x40 */
		PORT_BIT( 0x03, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Backspace", KEYCODE_BACKSPACE, IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "-", KEYCODE_MINUS, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, ":", KEYCODE_COLON, IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "0", KEYCODE_0, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "9", KEYCODE_9, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "8", KEYCODE_8, IP_JOY_NONE );
		PORT_START();  /* 2: DF00 & 0x20 */
		PORT_BIT( 0x07, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Enter", KEYCODE_ENTER, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "\\", KEYCODE_BACKSLASH, IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "O", KEYCODE_O, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "L", KEYCODE_L, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, ".", KEYCODE_STOP, IP_JOY_NONE );
		PORT_START();  /* 3: DF00 & 0x10 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "I", KEYCODE_I, IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "U", KEYCODE_U, IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "Y", KEYCODE_Y, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "T", KEYCODE_T, IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "R", KEYCODE_R, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "E", KEYCODE_E, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "W", KEYCODE_W, IP_JOY_NONE );
		PORT_START();  /* 4: DF00 & 0x08 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "K", KEYCODE_K, IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "J", KEYCODE_J, IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "H", KEYCODE_H, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "G", KEYCODE_G, IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "F", KEYCODE_F, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "D", KEYCODE_D, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "S", KEYCODE_S, IP_JOY_NONE );
		PORT_START();  /* 5: DF00 & 0x04 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, ",", KEYCODE_COMMA, IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "M", KEYCODE_M, IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "N", KEYCODE_N, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "B", KEYCODE_B, IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "V", KEYCODE_V, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "C", KEYCODE_C, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "X", KEYCODE_X, IP_JOY_NONE );
		PORT_START();  /* 6: DF00 & 0x02 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "P", KEYCODE_P, IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "'", KEYCODE_QUOTE, IP_JOY_NONE );
		PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "/", KEYCODE_SLASH, IP_JOY_NONE );
		PORT_BITX( 0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "Space", KEYCODE_SPACE, IP_JOY_NONE );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "Z", KEYCODE_Z, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "A", KEYCODE_A, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "Q", KEYCODE_Q, IP_JOY_NONE );
		PORT_START();  /* 7: DF00 & 0x01 */
		PORT_BITX( 0x01, IP_ACTIVE_LOW, IPT_KEYBOARD | IPF_TOGGLE, "Caps Lock", KEYCODE_CAPSLOCK, IP_JOY_NONE );
		PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "Right Shift", KEYCODE_RSHIFT, IP_JOY_NONE );
		PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Left Shift", KEYCODE_LSHIFT, IP_JOY_NONE );
		PORT_BIT( 0x18, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "Escape", KEYCODE_ESC, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "Control", KEYCODE_LCONTROL, IP_JOY_NONE );
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "Control", KEYCODE_RCONTROL, IP_JOY_NONE );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "~", KEYCODE_TILDE, IP_JOY_NONE );
		PORT_START(); 	/* 8: Machine config */
		PORT_DIPNAME( 0x03, 0, "RAM Size" );
		PORT_DIPSETTING( 0, "4Kb" );
		PORT_DIPSETTING( 1, "8Kb" );
		PORT_DIPSETTING( 2, "40Kb" );
	INPUT_PORTS_END(); }}; 
	
	/* sound output */
	
	/* machine definition */
	
	static MachineDriver machine_driver_uk101 = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6502,
				1000000,
				uk101_readmem, uk101_writemem,
				null, null,
				interrupt, 1,
			),
		},
		50, 2500,
		1,
		uk101_init_machine,
		uk101_stop_machine,
		32 * 8,
		25 * 16,
		new rectangle( 0, 32 * 8 - 1, 0, 25 * 16 - 1 ),
		uk101_gfxdecodeinfo,
		sizeof (uk101_palette) / 3,
		sizeof (uk101_colortable),
		uk101_init_palette,
		VIDEO_TYPE_RASTER,
		null,
		uk101_vh_start,
		uk101_vh_stop,
		uk101_vh_screenrefresh,
		0, 0, 0, 0,
	);
	
	static MachineDriver machine_driver_superbrd = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6502,
				1000000,
				superbrd_readmem, superbrd_writemem,
				null, null,
				interrupt, 1,
			),
		},
		50, 2500,
		1,
		uk101_init_machine,
		uk101_stop_machine,
		64 * 8,
		16 * 16,
		new rectangle( 0, 64 * 8 - 1, 0, 16 * 16 - 1 ),
		uk101_gfxdecodeinfo,
		sizeof (uk101_palette) / 3,
		sizeof (uk101_colortable),
		uk101_init_palette,
		VIDEO_TYPE_RASTER,
		null,
		uk101_vh_start,
		uk101_vh_stop,
		superbrd_vh_screenrefresh,
		0, 0, 0, 0,
	);
	
	static RomLoadPtr rom_uk101 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000, REGION_CPU1);
		ROM_LOAD("basuk01.rom", 0xa000, 0x0800, 0x9d3caa92);
		ROM_LOAD("basuk02.rom", 0xa800, 0x0800, 0x0039ef6a);
		ROM_LOAD("basuk03.rom", 0xb000, 0x0800, 0x0d011242);
		ROM_LOAD("basuk04.rom", 0xb800, 0x0800, 0x667223e8);
		ROM_LOAD("monuk02.rom", 0xf800, 0x0800, 0x04ac5822);
		ROM_REGION(0x800, REGION_GFX1);
		ROM_LOAD("chguk101.rom", 0x0000, 0x0800, 0xfce2c84a);
	ROM_END(); }}; 
	
	static RomLoadPtr rom_superbrd = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000, REGION_CPU1);
		ROM_LOAD("basus01.rom", 0xa000, 0x0800, 0xf4f5dec0);
		ROM_LOAD("basus02.rom", 0xa800, 0x0800, 0x0039ef6a);
		ROM_LOAD("basus03.rom", 0xb000, 0x0800, 0xca25f8c1);
		ROM_LOAD("basus04.rom", 0xb800, 0x0800, 0x8ee6030e);
		ROM_LOAD("monus02.rom", 0xf800, 0x0800, 0xe5b7028d);
		ROM_REGION(0x800, REGION_GFX1);
		ROM_LOAD("chgsuper.rom", 0x0000, 0x0800, BADCRC(0x136b5018);
	ROM_END(); }}; 
	
	static	const	struct	IODevice	io_uk101[] =
	{
		{
			IO_CASSETTE,			/* type */
			1,						/* count */
			"bas\0",				/* file extn */
			IO_RESET_NONE,			/* reset if file changed */
	        NULL,                   /* id */
			uk101_init_cassette,	/* init */
			uk101_exit_cassette,	/* exit */
			NULL,					/* info */
			NULL,					/* open */
			NULL,					/* close */
			NULL,					/* status */
			NULL,					/* seek */
			NULL,					/* tell */
			NULL,					/* input */
			NULL,					/* output */
			NULL,					/* input_chunk */
			NULL					/* output_chunk */
		},
		{ IO_END }
	};
	
	static	const	struct	IODevice io_superbrd[] =
	{
		{
			IO_CASSETTE,			/* type */
			1,						/* count */
			"bas\0",				/* file extn */
			IO_RESET_NONE,			/* reset if file changed */
	        NULL,                   /* id */
			uk101_init_cassette,	/* init */
			uk101_exit_cassette,	/* exit */
			NULL,					/* info */
			NULL,					/* open */
			NULL,					/* close */
			NULL,					/* status */
			NULL,					/* seek */
			NULL,					/* tell */
			NULL,					/* input */
			NULL,					/* output */
			NULL,					/* input_chunk */
			NULL					/* output_chunk */
		},
		{ IO_END }
	};
	
	/*    YEAR	NAME		PARENT	MACHINE		INPUT	INIT	COMPANY				FULLNAME */
	COMP( 1979,	uk101,		0,		uk101,		uk101,	0,		"Compukit",			"UK101" )
	COMP( 1979, superbrd,	uk101,	superbrd,	uk101,	0,		"Ohio Scientific",	"Superboard II" )
	
}
