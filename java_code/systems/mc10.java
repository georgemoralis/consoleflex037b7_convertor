/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class mc10
{
	
	extern int coco_cassette_init(int id);
	extern void coco_cassette_exit(int id);
	
	static MemoryReadAddress mc10_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x001f, m6803_internal_registers_r ),
		new MemoryReadAddress( 0x0020, 0x007f, MRA_NOP ), /* unused */
		new MemoryReadAddress( 0x0080, 0x00ff, MRA_RAM ), /* 6803 internal RAM */
		new MemoryReadAddress( 0x0100, 0x3fff, MRA_NOP ), /* unused */
		new MemoryReadAddress( 0x4000, 0x4fff, MRA_RAM ),
	//	new MemoryReadAddress( 0x5000, 0xbffe, MRA_RAM ), /* expansion RAM */
		new MemoryReadAddress( 0xbfff, 0xbfff, mc10_bfff_r ),
	//	new MemoryReadAddress( 0xc000, 0xdfff, MWA_ROM ), /* expansion ROM */
		new MemoryReadAddress( 0xe000, 0xffff, MRA_ROM ), /* ROM */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress mc10_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x001f, m6803_internal_registers_w ),
		new MemoryWriteAddress( 0x0020, 0x007f, MWA_NOP ), /* unused */
		new MemoryWriteAddress( 0x0080, 0x00ff, MWA_RAM ), /* 6803 internal RAM */
		new MemoryWriteAddress( 0x0100, 0x3fff, MWA_NOP ), /* unused */
		new MemoryWriteAddress( 0x4000, 0x4fff, mc10_ram_w ),
	//	new MemoryWriteAddress( 0x5000, 0xbffe, MWA_RAM ), /* expansion RAM */
		new MemoryWriteAddress( 0xbfff, 0xbfff, mc10_bfff_w ),
	//	new MemoryWriteAddress( 0xc000, 0xdfff, MWA_ROM ), /* expansion ROM */
		new MemoryWriteAddress( 0xe000, 0xffff, MWA_ROM ), /* ROM */
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	static IOReadPort mc10_readport[] =
	{
		new IOReadPort( M6803_PORT1, M6803_PORT1, mc10_port1_r ),
		new IOReadPort( M6803_PORT2, M6803_PORT2, mc10_port2_r ),
		new IOReadPort( -1 )	/* end of table */
	};
	
	static IOWritePort mc10_writeport[] =
	{
		new IOWritePort( M6803_PORT1, M6803_PORT1, mc10_port1_w ),
		new IOWritePort( M6803_PORT2, M6803_PORT2, mc10_port2_w ),
		new IOWritePort( -1 )	/* end of table */
	};
	
	/* MC-10 keyboard
	
		   PB0 PB1 PB2 PB3 PB4 PB5 PB6 PB7
	  PA6: Ctl N/c Brk N/c N/c N/c N/c Shift
	  PA5: 8   9   :   ;   ,   -   .   /
	  PA4: 0   1   2   3   4   5   6   7
	  PA3: X   Y   Z   N/c N/c N/c Ent Space
	  PA2: P   Q   R   S   T   U   V   W
	  PA1: H   I   J   K   L   M   N   O
	  PA0: @   A   B   C   D   E   F   G
	 */
	static InputPortPtr input_ports_mc10 = new InputPortPtr(){ public void handler() { 
		PORT_START();  /* KEY ROW 0 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "@", KEYCODE_ASTERISK, IP_JOY_NONE);
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "A", KEYCODE_A, IP_JOY_NONE);
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "B", KEYCODE_B, IP_JOY_NONE);
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "C", KEYCODE_C, IP_JOY_NONE);
		PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "D", KEYCODE_D, IP_JOY_NONE);
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "E", KEYCODE_E, IP_JOY_NONE);
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "F", KEYCODE_F, IP_JOY_NONE);
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "G", KEYCODE_G, IP_JOY_NONE);
	
		PORT_START();  /* KEY ROW 1 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "H", KEYCODE_H, IP_JOY_NONE);
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "I", KEYCODE_I, IP_JOY_NONE);
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "J", KEYCODE_J, IP_JOY_NONE);
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "K", KEYCODE_K, IP_JOY_NONE);
		PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "L", KEYCODE_L, IP_JOY_NONE);
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "M", KEYCODE_M, IP_JOY_NONE);
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "N", KEYCODE_N, IP_JOY_NONE);
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "O", KEYCODE_O, IP_JOY_NONE);
	
		PORT_START();  /* KEY ROW 2 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "P", KEYCODE_P, IP_JOY_NONE);
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "Q", KEYCODE_Q, IP_JOY_NONE);
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "R", KEYCODE_R, IP_JOY_NONE);
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "S", KEYCODE_S, IP_JOY_NONE);
		PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "T", KEYCODE_T, IP_JOY_NONE);
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "U", KEYCODE_U, IP_JOY_NONE);
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "V", KEYCODE_V, IP_JOY_NONE);
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "W", KEYCODE_W, IP_JOY_NONE);
	
		PORT_START();  /* KEY ROW 3 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "X", KEYCODE_X, IP_JOY_NONE);
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "Y", KEYCODE_Y, IP_JOY_NONE);
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "Z", KEYCODE_Z, IP_JOY_NONE);
		PORT_BITX(0x38, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "ENTER", KEYCODE_ENTER, IP_JOY_NONE);
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "SPACE", KEYCODE_SPACE, IP_JOY_NONE);
	
		PORT_START();  /* KEY ROW 4 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "0	  ", KEYCODE_0, IP_JOY_NONE);
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "1	 !", KEYCODE_1, IP_JOY_NONE);
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "2	 \"", KEYCODE_2, IP_JOY_NONE);
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, "3	 #", KEYCODE_3, IP_JOY_NONE);
		PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, "4	 $", KEYCODE_4, IP_JOY_NONE);
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "5	 %", KEYCODE_5, IP_JOY_NONE);
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "6	 &", KEYCODE_6, IP_JOY_NONE);
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "7	 '", KEYCODE_7, IP_JOY_NONE);
	
		PORT_START();  /* KEY ROW 5 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "8	 (", KEYCODE_8, IP_JOY_NONE);
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_KEYBOARD, "9	 );, KEYCODE_9, IP_JOY_NONE)
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, ":	 *", KEYCODE_COLON, IP_JOY_NONE);
		PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_KEYBOARD, ";	 +", KEYCODE_QUOTE, IP_JOY_NONE);
		PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_KEYBOARD, ",	 <", KEYCODE_COMMA, IP_JOY_NONE);
		PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_KEYBOARD, "-	 =", KEYCODE_MINUS, IP_JOY_NONE);
		PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, ".	 >", KEYCODE_STOP, IP_JOY_NONE);
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "/	 ?", KEYCODE_SLASH, IP_JOY_NONE);
	
		PORT_START();  /* KEY ROW 6 */
		PORT_BITX(0x01, IP_ACTIVE_LOW, IPT_KEYBOARD, "CONTROL", KEYCODE_LCONTROL, IP_JOY_NONE);
		PORT_BITX(0x02, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)
		PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_KEYBOARD, "BREAK", KEYCODE_END, IP_JOY_NONE);
		PORT_BITX(0x78, IP_ACTIVE_LOW, IPT_UNUSED, DEF_STR( "Unused") ); IP_KEY_NONE, IP_JOY_NONE)
		PORT_BITX(0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "SHIFT", KEYCODE_LSHIFT, IP_JOY_NONE);
	
		PORT_START();  /* 7 */
		PORT_DIPNAME( 0x80, 0x00, "16K RAM module" );
		PORT_DIPSETTING(	0x00, DEF_STR( "No") );
		PORT_DIPSETTING(	0x80, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x00, "DOS extension" );
		PORT_DIPSETTING(	0x00, DEF_STR( "No") );
		PORT_DIPSETTING(	0x40, DEF_STR( "Yes") );
		PORT_BIT(	  0x3c, 0x3c, IPT_UNUSED );
		PORT_DIPNAME( 0x03, 0x01, "Artifacting" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, "Red" );
		PORT_DIPSETTING(    0x02, "Blue" );
	
	INPUT_PORTS_END(); }}; 
	
	static DACinterface mc10_dac_interface = new DACinterface
	(
		1,
		new int[] { 100 }
	);
	
	static MachineDriver machine_driver_mc10 = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6803,
				894886,	/* 0,894886 Mhz */
				mc10_readmem,mc10_writemem,
				mc10_readport, mc10_writeport,
				mc10_interrupt, 1,
				0, 0,
			),
		},
		60, 0,		 /* frames per second, vblank duration */
		null,
		mc10_init_machine,
		mc10_stop_machine,
	
		/* video hardware */
		32*8,										/* screen width */
		16*12,									/* screen height (pixels doubled) */
		new rectangle( 0, 32*8-1, 0, 16*12-1),					/* visible_area */
		null,							/* graphics decode info */
		M6847_TOTAL_COLORS,
		null,
		m6847_vh_init_palette,						/* initialise palette */
	
		VIDEO_TYPE_RASTER | VIDEO_SUPPORTS_DIRTY,
		null,
		mc10_vh_start,
		m6847_vh_stop,
		m6847_vh_update,
	
		/* sound hardware */
		0, 0, 0, 0,
		new MachineSound[] {
			new MachineSound(
				SOUND_DAC,
				mc10_dac_interface
			)
		}
	);
	
	static RomLoadPtr rom_mc10 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000,REGION_CPU1);
		ROM_LOAD("mc10.rom", 0xE000, 0x2000, 0x11fda97e);
	ROM_END(); }}; 
	
	static const struct IODevice io_mc10[] = {
		IO_CASSETTE_WAVE(1, "cas\0wav\0", NULL, coco_cassette_init, coco_cassette_exit),
	    { IO_END }
	};
	
	COMP( 1983, mc10,     0,        mc10,     mc10,     0,        "Tandy Radio Shack",  "MC-10" )
	
}
