/***************************************************************************

  /systems/odyssey2.c

  Driver file to handle emulation of the Odyssey2.

***************************************************************************/

/**********************************************
8048 Ports:???????????
P1 	Bit 0..1  - RAM bank select
	Bit 3..7  - Keypad input:

P2 	Bit 0..3  - A8-A11
	Bit 4..7  - Sound control

T1	Mirror sync pulse

***********************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package systems;

public class odyssey2
{
	
	static MemoryReadAddress readmem[] =
	{
	    new MemoryReadAddress( 0x0000, 0x03FF, MRA_ROM ),
	    new MemoryReadAddress( 0x0400, 0x14FF, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
	    new MemoryWriteAddress( 0x0000, 0x03FF, MWA_ROM ),
	    new MemoryWriteAddress( 0x0400, 0x14FF, MWA_ROM ),
	    new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static IOReadPort readport[] =
	{
	    new IOReadPort( 0x00,     0xff,     odyssey2_MAINRAM_r),
	    new IOReadPort( I8039_p1, I8039_p1, odyssey2_getp1 ),
		new IOReadPort( -1 )	/* end of table */
	};
	
	static IOWritePort writeport[] =
	{
	    new IOWritePort( 0x00,     0xff,     odyssey2_MAINRAM_w ),
	    new IOWritePort( I8039_p1, I8039_p1, odyssey2_putp1 ),
		new IOWritePort( -1 )	/* end of table */
	};
	
	
	static InputPortPtr input_ports_odyssey2 = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
	INPUT_PORTS_END(); }}; 
	
	static MachineDriver machine_driver_odyssey2 = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
	            CPU_I8048,
	            1790000,  /* 1.79 MHz */
	            readmem,writemem,readport,writeport,
				ignore_interrupt,1
			)
		},
		8*15, DEFAULT_REAL_60HZ_VBLANK_DURATION,
		1,
		odyssey2_init_machine,	/* init_machine */
		null,						/* stop_machine */
	
		/* video hardware */
		262,240, new rectangle(0,262-1,0,240-1),
		NULL,
		(8+2)*3,
		8*2,
		odyssey2_vh_init_palette,
	
		VIDEO_TYPE_RASTER,
		null,
	    odyssey2_vh_start,
	    odyssey2_vh_stop,
	    odyssey2_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
	);
	
	
	ROM_START (odyssey2)
		ROM_REGION(0x10000,REGION_CPU1);   /* 64 k Internal RAM */
	    ROM_LOAD ("o2bios.rom", 0x0000, 0x400, 0x8016a315);
	ROM_END(); }}; 
	
	static const struct IODevice io_odyssey2[] = {
		{
			IO_CARTSLOT,		/* type */
			1,					/* count */
			"bin\0",            /* file extensions */
			NULL,				/* private */
			NULL,	            /* id */
			odyssey2_load_rom, 	/* init */
			NULL,				/* exit */
			NULL,				/* info */
			NULL,				/* open */
			NULL,				/* close */
			NULL,				/* status */
			NULL,				/* seek */
			NULL,				/* tell */
	        NULL,               /* input */
			NULL,				/* output */
			NULL,				/* input_chunk */
			NULL				/* output_chunk */
	    },
	    { IO_END }
	};
	
	/*    YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY   FULLNAME */
	CONSX( 1982, odyssey2, 0,		odyssey2, odyssey2,	0,		  "Magnavox",  "ODYSSEY 2", GAME_NO_SOUND )
	
}
