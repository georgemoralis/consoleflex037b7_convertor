/*
  Experimental ti990/4 driver

  We emulate a ti990/4 set with a LOAD ROM.

  4kb + 512 bytes RAM ?

  512 bytes ROM

  The driver runs the boot ROM OK.  Unfortunately, no boot device is emulated.

TODO :
* programmer panel
* disk, tape, 911 vdt
* 990/10

*/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class ti990_4
{
	
	static int ROM_paged;
	
	void *timer;
	
	static void clear_load(int dummy)
	{
		cpu_set_nmi_line(0, CLEAR_LINE);
	}
	
	static public static InitMachinePtr ti990_4_init_machine = new InitMachinePtr() { public void handler() 
	{
		cpu_set_nmi_line(0, ASSERT_LINE);
		timer = timer_set(TIME_IN_MSEC(100), 0, clear_load);
	} };
	
	static void ti990_4_stop_machine(void)
	{
	
	}
	
	public static InterruptPtr ti990_4_vblank_interrupt = new InterruptPtr() { public int handler() 
	{
	
	
		return ignore_interrupt();
	} };
	
	/*
	three panel types
	* operator panel
	* programmer panel
	* MDU
	
	Operator panel :
	* Power led
	* Fault led
	* Off/On/Load switch
	
	Programmer panel :
	* 16 status light, 32 switches, IDLE, RUN led
	* interface to a low-level debugger in ROMs
	
	* MDU :
	* includes a tape unit, possibly other stuff
	
	output :
	0-7 : lights 0-7
	8 : increment scan
	9 : clear scan
	A : run light
	B : fault light
	C : Memory Error Interrupt clear
	D : Start panel timer
	E : Set SIE function (interrupt after 2 instructions are executed)
	F : flag
	
	input :
	0-7 : switches 0-7 (or data from MDU tape)
	8 : scan count bit 1
	9 : scan count bit 0
	A : timer active
	B : programmer panel not present
	C : char in MDU tape unit buffer ?
	D : unused ?
	E : MDU tape unit present ?
	F : flag
	
	*/
	
	public static ReadHandlerPtr ti990_4_panel_read  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (offset == 1)
			return 0x08;
	
		return 0;
	} };
	
	public static WriteHandlerPtr ti990_4_panel_write = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	} };
	
	/*
	  TI990/4 video emulation.
	
	  I guess there was text terminal and CRT terminals.
	*/
	
	
	static void ti990_4_init_palette(UBytePtr palette, unsigned short *colortable, const UBytePtr dummy)
	{
	/*	memcpy(palette, & ti990_4_palette, sizeof(ti990_4_palette));
		memcpy(colortable, & ti990_4_colortable, sizeof(ti990_4_colortable));*/
	}
	
	static public static VhStartPtr ti990_4_vh_start = new VhStartPtr() { public int handler() 
	{
		return 0; /*generic_vh_start();*/
	} };
	
	/*#define ti990_4_vh_stop generic_vh_stop*/
	
	static public static VhStopPtr ti990_4_vh_stop = new VhStopPtr() { public void handler() 
	{
	} };
	
	static void ti990_4_vh_refresh(struct osd_bitmap *bitmap, int full_refresh)
	{
	
	}
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( -1 )	/* end of array */
	};
	
	
	/*
	  Memory map - see description above
	*/
	
	static MemoryReadAddress ti990_4_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_RAM ),		/* dynamic RAM ? */
		new MemoryReadAddress( 0x2000, 0xf7ff, MRA_NOP ),		/* reserved for expansion */
		new MemoryReadAddress( 0xf800, 0xfbff, MRA_RAM ),		/* static RAM ? */
		new MemoryReadAddress( 0xfc00, 0xffff, MRA_ROM ),		/* LOAD ROM */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress ti990_4_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_RAM ),		/* dynamic RAM ? */
		new MemoryWriteAddress( 0x2000, 0xf7ff, MWA_NOP ),		/* reserved for expansion */
		new MemoryWriteAddress( 0xf800, 0xfbff, MWA_RAM ),		/* static RAM ? */
		new MemoryWriteAddress( 0xfc00, 0xffff, MWA_ROM ),		/* LOAD ROM */
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	/*
	  CRU map
	*/
	
	static IOWritePort ti990_4_writeport[] =
	{
		new IOWritePort( 0xff0, 0xfff, ti990_4_panel_write ),
		new IOWritePort( -1 )	/* end of table */
	};
	
	static IOReadPort ti990_4_readport[] =
	{
		new IOReadPort( 0x1fe, 0x1ff, ti990_4_panel_read ),
		new IOReadPort( -1 )	/* end of table */
	};
	
	static MachineDriver machine_driver_ti990_4 = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_TMS9900,
				3000000,	/* unknown */
				ti990_4_readmem, ti990_4_writemem, ti990_4_readport, ti990_4_writeport,
				ti990_4_vblank_interrupt, 1,
				0, 0,
				0
			),
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION, /* frames per second, vblank duration */
		1,
		ti990_4_init_machine,
		ti990_4_stop_machine,
	
		/* video hardware - no screen emulated */
		200,						/* screen width */
		200,						/* screen height */
		new rectangle( 0, 200-1, 0, 200-1),		/* visible_area */
		gfxdecodeinfo,				/* graphics decode info (???)*/
		null/*TI990_4_PALETTE_SIZE*/,		/* palette is 3*total_colors bytes long */
		null/*TI990_4_COLORTABLE_SIZE*/,	/* length in shorts of the color lookup table */
		ti990_4_init_palette,		/* palette init */
	
		VIDEO_TYPE_RASTER,
		null,
		ti990_4_vh_start,
		ti990_4_vh_stop,
		ti990_4_vh_refresh,
	
		/* sound hardware */
		0,
		0,0,0,
	
	#if 0
		new MachineSound[] { /* no sound ! */
		}
	#endif
	);
	
	
	/*
	  ROM loading
	*/
	static RomLoadPtr rom_ti990_4 = new RomLoadPtr(){ public void handler(){ 
		/*CPU memory space*/
	
	#if 0
	
	#if 0
	
		ROM_REGION(0x10000, REGION_CPU1);
	
		/* TI990/10 ROMs set 1 */
		ROM_LOAD_EVEN("975383.31", 0xFC00, 0x100, 0x64fcd040);
		ROM_LOAD_ODD("975383.32", 0xFC00, 0x100, 0x64277276);
		ROM_LOAD_EVEN("975383.29", 0xFE00, 0x100, 0xaf92e7bf);
		ROM_LOAD_ODD("975383.30", 0xFE00, 0x100, 0xb7b40cdc);
	
	#elif 1
	
		ROM_REGION(0x10000, REGION_CPU1);
	
		/* TI990/10 ROMs set 2 */
		ROM_LOAD_EVEN("975383.45", 0xFC00, 0x100, 0x391943c7);
		ROM_LOAD_ODD("975383.46", 0xFC00, 0x100, 0xf40f7c18);
		ROM_LOAD_EVEN("975383.47", 0xFE00, 0x100, 0x1ba571d8);
		ROM_LOAD_ODD("975383.48", 0xFE00, 0x100, 0x8852b09e);
	
	#else
	
		ROM_REGION(0x12000, REGION_CPU1);
	
		/* TI990/12 ROMs - actually incompatible with TI990/4, but I just wanted to disassemble them. */
		ROM_LOAD_EVEN("ti2025-7", 0xFC00, 0x1000, 0x4824f89c);
		ROM_LOAD_ODD("ti2025-8", 0xFC00, 0x1000, 0x51fef543);
		/* the other half of this ROM is not loaded - it makes no sense, anyway... */
	
	#endif
	
	#else
	
		ROM_REGION(0x10000, REGION_CPU1);
	
	
		ROM_REGION(0x800, REGION_USER1 | REGIONFLAG_DISPOSE);
		/* boot ROMs */
		/* since there is no support for nibble-wide ROMs on a 16-bit bus, we use a trick */
	
		/* test ROM */
		ROM_LOAD("94519209.u39", 0x000, 0x100, 0x0a0b0c42);
		ROM_LOAD("94519210.u55", 0x100, 0x100, 0xd078af61);
		ROM_LOAD("94519211.u61", 0x200, 0x100, 0x6cf7d4a0);
		ROM_LOAD("94519212.u78", 0x300, 0x100, 0xd9522458);
	
		/* LOAD ROM */
		ROM_LOAD("94519113.u3", 0x400, 0x100, 0x8719b04e);
		ROM_LOAD("94519114.u4", 0x500, 0x100, 0x72a040e0);
		ROM_LOAD("94519115.u6", 0x600, 0x100, 0x9ccf8cca);
		ROM_LOAD("94519116.u7", 0x700, 0x100, 0xfa387bf3);
	
	#endif
	ROM_END(); }}; 
	
	static void ti990_4_load_rom(void)
	{
	#if 1
		int i;
		UBytePtr ROM = memory_region(REGION_CPU1);
		UBytePtr src = memory_region(REGION_USER1);
	
		for (i=0; i<256; i++)
		{
			WRITE_WORD(ROM + 0xFC00 + i*2, (((int) src[0x000+i]) << 12) | (((int) src[0x100+i]) << 8)
			                        | (((int) src[0x200+i]) << 4) | (((int) src[0x300+i]) << 0) );
	
			WRITE_WORD(ROM + 0xFE00 + i*2, (((int) src[0x400+i]) << 12) | (((int) src[0x500+i]) << 8)
			                        | (((int) src[0x600+i]) << 4) | (((int) src[0x700+i]) << 0) );
		}
	
	#endif
	}
	
	static public static InitDriverPtr init_ti990_4 = new InitDriverPtr() { public void handler() 
	{
		ti990_4_load_rom();
	} };
	
	static const struct IODevice io_ti990_4[] =
	{
		/* of course, there was I/O devices, but I am not advanced enough... */
		{ IO_END }
	};
	
	static InputPortPtr input_ports_ti990_4 = new InputPortPtr(){ public void handler() { 
	INPUT_PORTS_END(); }}; 
	
	/*		YEAR				NAME			PARENT	MACHINE		INPUT	INIT	COMPANY	FULLNAME */
	COMP( circa 1975,	ti990_4,	0,			ti990_4,	ti990_4,	ti990_4,	"Texas Instruments",	"TI990/4" )
	
	
	
}
