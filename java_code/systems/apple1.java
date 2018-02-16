/**********************************************************************
Apple I Memory map

	CPU: 6502 @ .960Mhz

		0000-1FFF	RAM
		2000-D00F	NOP
		D010-D013	PIA6820
		D014-FEFF	NOP
		FF00-FFFF	ROM

Interrupts:	None.

Video:		1K x 7 shift registers

Sound:		None

Hardware:	PIA6820 DSP for keyboard and screen interface

		d010	KEYBOARD DDR	Returns 7 bit ascii key
		d011	KEYBOARD CTR	Bit 7 high signals available key
		d012	DISPLAY DDR	Output to screen, set bit 7 of d013
		d013	DISPLAY CTR	Bit 7 low signals display ready
**********************************************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class apple1
{
	
	/* port i/o functions */
	
	/* memory w/r functions */
	
	static MemoryReadAddress apple1_readmem[] =
	{
		new MemoryReadAddress(0x0000, 0x1fff, MRA_RAM),
		new MemoryReadAddress(0x2000, 0xcfff, MRA_NOP),
		new MemoryReadAddress(0xd000, 0xd00f, MRA_NOP),
		new MemoryReadAddress(0xd010, 0xd013, pia_0_r),
		new MemoryReadAddress(0xd014, 0xfeff, MRA_NOP),
		new MemoryReadAddress(0xff00, 0xffff, MRA_ROM),
		new MemoryReadAddress(-1)
	};
	
	static MemoryWriteAddress apple1_writemem[] =
	{
		new MemoryWriteAddress(0x0000, 0x1fff, MWA_RAM),
		new MemoryWriteAddress(0x2000, 0xcfff, MWA_NOP),
		new MemoryWriteAddress(0xd000, 0xd00f, MWA_NOP),
		new MemoryWriteAddress(0xd010, 0xd013, pia_0_w),
		new MemoryWriteAddress(0xd014, 0xfeff, MWA_NOP),
		new MemoryWriteAddress(0xff00, 0xffff, MWA_ROM),
		new MemoryWriteAddress(-1)
	};
	
	/* graphics output */
	
	static GfxLayout apple1_charlayout = new GfxLayout
	(
		6, 8,
		128,
		1,
		new int[] { 0 },
		new int[] { 0, 1, 2, 3, 4, 5 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8 * 8
	);
	
	static GfxDecodeInfo apple1_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0x0000, apple1_charlayout, 0, 1),
		new GfxDecodeInfo( -1 )
	};
	
	static unsigned char apple1_palette[] =
	{
		0x00, 0x00, 0x00,	/* Black */
		0x00, 0xff, 0x00	/* Green */
	};
	
	static unsigned short apple1_colortable[] =
	{
		0, 1
	};
	
	static void apple1_init_palette (UBytePtr sys_palette,
						unsigned short *sys_colortable,
						const UBytePtr color_prom)
	{
		memcpy (sys_palette, apple1_palette, sizeof (apple1_palette));
		memcpy (sys_colortable, apple1_colortable, sizeof (apple1_colortable));
	}
	
	/* keyboard input */
	
	static InputPortPtr input_ports_apple1 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* 0: first sixteen keys */
		PORT_BITX( 0x0001, IP_ACTIVE_HIGH, IPT_KEYBOARD, "0", KEYCODE_0, IP_JOY_NONE );
		PORT_BITX( 0x0002, IP_ACTIVE_HIGH, IPT_KEYBOARD, "1", KEYCODE_1, IP_JOY_NONE );
		PORT_BITX( 0x0004, IP_ACTIVE_HIGH, IPT_KEYBOARD, "2", KEYCODE_2, IP_JOY_NONE );
		PORT_BITX( 0x0008, IP_ACTIVE_HIGH, IPT_KEYBOARD, "3", KEYCODE_3, IP_JOY_NONE );
		PORT_BITX( 0x0010, IP_ACTIVE_HIGH, IPT_KEYBOARD, "4", KEYCODE_4, IP_JOY_NONE );
		PORT_BITX( 0x0020, IP_ACTIVE_HIGH, IPT_KEYBOARD, "5", KEYCODE_5, IP_JOY_NONE );
		PORT_BITX( 0x0040, IP_ACTIVE_HIGH, IPT_KEYBOARD, "6", KEYCODE_6, IP_JOY_NONE );
		PORT_BITX( 0x0080, IP_ACTIVE_HIGH, IPT_KEYBOARD, "7", KEYCODE_7, IP_JOY_NONE );
		PORT_BITX( 0x0100, IP_ACTIVE_HIGH, IPT_KEYBOARD, "8", KEYCODE_8, IP_JOY_NONE );
		PORT_BITX( 0x0200, IP_ACTIVE_HIGH, IPT_KEYBOARD, "9", KEYCODE_9, IP_JOY_NONE );
		PORT_BITX( 0x0400, IP_ACTIVE_HIGH, IPT_KEYBOARD, "-", KEYCODE_MINUS, IP_JOY_NONE );
		PORT_BITX( 0x0800, IP_ACTIVE_HIGH, IPT_KEYBOARD, "=", KEYCODE_EQUALS, IP_JOY_NONE );
		PORT_BITX( 0x1000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "[", KEYCODE_OPENBRACE, IP_JOY_NONE );
		PORT_BITX( 0x2000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "]", KEYCODE_CLOSEBRACE, IP_JOY_NONE );
		PORT_BITX( 0x4000, IP_ACTIVE_HIGH, IPT_KEYBOARD, ";", KEYCODE_COLON, IP_JOY_NONE );
		PORT_BITX( 0x8000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "'", KEYCODE_QUOTE, IP_JOY_NONE );
		PORT_START(); 	/* 1: second sixteen keys */
		PORT_BITX( 0x0001, IP_ACTIVE_HIGH, IPT_KEYBOARD, "#", KEYCODE_TILDE, IP_JOY_NONE );
		PORT_BITX( 0x0002, IP_ACTIVE_HIGH, IPT_KEYBOARD, ",", KEYCODE_COMMA, IP_JOY_NONE );
		PORT_BITX( 0x0004, IP_ACTIVE_HIGH, IPT_KEYBOARD, ".", KEYCODE_STOP, IP_JOY_NONE );
		PORT_BITX( 0x0008, IP_ACTIVE_HIGH, IPT_KEYBOARD, "/", KEYCODE_SLASH, IP_JOY_NONE );
		PORT_BITX( 0x0010, IP_ACTIVE_HIGH, IPT_KEYBOARD, "\\", KEYCODE_BACKSLASH, IP_JOY_NONE );
		PORT_BITX( 0x0020, IP_ACTIVE_HIGH, IPT_KEYBOARD, "A", KEYCODE_A, IP_JOY_NONE );
		PORT_BITX( 0x0040, IP_ACTIVE_HIGH, IPT_KEYBOARD, "B", KEYCODE_B, IP_JOY_NONE );
		PORT_BITX( 0x0080, IP_ACTIVE_HIGH, IPT_KEYBOARD, "C", KEYCODE_C, IP_JOY_NONE );
		PORT_BITX( 0x0100, IP_ACTIVE_HIGH, IPT_KEYBOARD, "D", KEYCODE_D, IP_JOY_NONE );
		PORT_BITX( 0x0200, IP_ACTIVE_HIGH, IPT_KEYBOARD, "E", KEYCODE_E, IP_JOY_NONE );
		PORT_BITX( 0x0400, IP_ACTIVE_HIGH, IPT_KEYBOARD, "F", KEYCODE_F, IP_JOY_NONE );
		PORT_BITX( 0x0800, IP_ACTIVE_HIGH, IPT_KEYBOARD, "G", KEYCODE_G, IP_JOY_NONE );
		PORT_BITX( 0x1000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "H", KEYCODE_H, IP_JOY_NONE );
		PORT_BITX( 0x2000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "I", KEYCODE_I, IP_JOY_NONE );
		PORT_BITX( 0x4000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "J", KEYCODE_J, IP_JOY_NONE );
		PORT_BITX( 0x8000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "K", KEYCODE_K, IP_JOY_NONE );
		PORT_START(); 	/* 2: third sixteen keys */
		PORT_BITX( 0x0001, IP_ACTIVE_HIGH, IPT_KEYBOARD, "L", KEYCODE_L, IP_JOY_NONE );
		PORT_BITX( 0x0002, IP_ACTIVE_HIGH, IPT_KEYBOARD, "M", KEYCODE_M, IP_JOY_NONE );
		PORT_BITX( 0x0004, IP_ACTIVE_HIGH, IPT_KEYBOARD, "N", KEYCODE_N, IP_JOY_NONE );
		PORT_BITX( 0x0008, IP_ACTIVE_HIGH, IPT_KEYBOARD, "O", KEYCODE_O, IP_JOY_NONE );
		PORT_BITX( 0x0010, IP_ACTIVE_HIGH, IPT_KEYBOARD, "P", KEYCODE_P, IP_JOY_NONE );
		PORT_BITX( 0x0020, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Q", KEYCODE_Q, IP_JOY_NONE );
		PORT_BITX( 0x0040, IP_ACTIVE_HIGH, IPT_KEYBOARD, "R", KEYCODE_R, IP_JOY_NONE );
		PORT_BITX( 0x0080, IP_ACTIVE_HIGH, IPT_KEYBOARD, "S", KEYCODE_S, IP_JOY_NONE );
		PORT_BITX( 0x0100, IP_ACTIVE_HIGH, IPT_KEYBOARD, "T", KEYCODE_T, IP_JOY_NONE );
		PORT_BITX( 0x0200, IP_ACTIVE_HIGH, IPT_KEYBOARD, "U", KEYCODE_U, IP_JOY_NONE );
		PORT_BITX( 0x0400, IP_ACTIVE_HIGH, IPT_KEYBOARD, "V", KEYCODE_V, IP_JOY_NONE );
		PORT_BITX( 0x0800, IP_ACTIVE_HIGH, IPT_KEYBOARD, "W", KEYCODE_W, IP_JOY_NONE );
		PORT_BITX( 0x1000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "X", KEYCODE_X, IP_JOY_NONE );
		PORT_BITX( 0x2000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Y", KEYCODE_Y, IP_JOY_NONE );
		PORT_BITX( 0x4000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Z", KEYCODE_Z, IP_JOY_NONE );
		PORT_BITX( 0x8000, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Enter", KEYCODE_ENTER, IP_JOY_NONE );
		PORT_START(); 	/* 3: fourth sixteen keys */
		PORT_BITX( 0x0001, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Backspace", KEYCODE_BACKSPACE, IP_JOY_NONE );
		PORT_BITX( 0x0002, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Space", KEYCODE_SPACE, IP_JOY_NONE );
		PORT_BITX( 0x0004, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Escape", KEYCODE_ESC, IP_JOY_NONE );
		PORT_BITX( 0x0008, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Shift", KEYCODE_LSHIFT, IP_JOY_NONE );
		PORT_BITX( 0x0010, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Shift", KEYCODE_RSHIFT, IP_JOY_NONE );
		PORT_BITX( 0x0020, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Reset", KEYCODE_F1, IP_JOY_NONE );
		PORT_BITX( 0x0040, IP_ACTIVE_HIGH, IPT_KEYBOARD, "Clear", KEYCODE_F2, IP_JOY_NONE );
		PORT_START(); 	/* 4: Machine config */
		PORT_DIPNAME( 0x01, 0, "RAM Size");
		PORT_DIPSETTING(0, "8Kb");
		PORT_DIPSETTING(1, "52Kb");
	INPUT_PORTS_END(); }}; 
	
	/* sound output */
	
	/* machine definition */
	
	static MachineDriver machine_driver_apple1 = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6502,
				960000,
				apple1_readmem, apple1_writemem,
				null, null,
				apple1_interrupt, 1,
			),
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
		1,
		apple1_init_machine,
		apple1_stop_machine,
		40 * 6,
		24 * 8,
		new rectangle( 0, 40 * 6 - 1, 0, 24 * 8 - 1 ),
		apple1_gfxdecodeinfo,
		sizeof (apple1_palette) / 3,
		sizeof (apple1_colortable),
		apple1_init_palette,
		VIDEO_TYPE_RASTER,
		null,
		apple1_vh_start,
		apple1_vh_stop,
		apple1_vh_screenrefresh,
		0, 0, 0, 0,
	);
	
	static RomLoadPtr rom_apple1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000, REGION_CPU1);
		ROM_LOAD("apple1.rom", 0xff00, 0x0100, 0xa30b6af5);
		ROM_REGION(0x0400, REGION_GFX1);
		ROM_LOAD("apple1.chr", 0x0000, 0x0400, 0xbe70bb85);
	ROM_END(); }}; 
	
	static	const	struct	IODevice io_apple1[] = {
		{ IO_END }
	};
	
	/*    YEAR	NAME	PARENT	MACHINE	INPUT	INIT	COMPANY				FULLNAME */
	COMP( 1976,	apple1,	0,		apple1,	apple1,	0,		"Apple Computer",	"Apple I" )
}
