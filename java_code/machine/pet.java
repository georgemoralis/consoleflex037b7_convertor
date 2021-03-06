/***************************************************************************
	commodore pet series computer

    peter.trauner@jk.uni-linz.ac.at
***************************************************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class pet
{
	
	#define VERBOSE_DBG 1
	
	/* keyboard lines */
	static UINT8 pet_keyline[10] = { 0 };
	static int pet_basic1=0; /* basic version 1 for quickloader */
	static int superpet=0;
	static int cbm8096=0;
	static int pet_keyline_select;
	static void *pet_clock;
	
	UINT8 *pet_memory;
	UINT8 *superpet_memory;
	UINT8 *pet_videoram;
	
	/* pia at 0xe810
	   port a
	    7 sense input (low for diagnostics)
	    6 ieee eoi in
	    5 cassette 2 switch in
	    4 cassette 1 switch in
	    3..0 keyboard line select
	
	  ca1 cassette 1 read
	  ca2 ieee eoi out
	
	  cb1 video sync in
	  cb2 cassette 1 motor out
	*/
	public static ReadHandlerPtr pet_pia0_port_a_read  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int data=0xff;
		if (!cbm_ieee_eoi_r()) data&=~0x40;
		return data;
	} };
	
	public static WriteHandlerPtr pet_pia0_port_a_write = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		pet_keyline_select=data;  /*data is actually line here! */
	} };
	
	public static ReadHandlerPtr pet_pia0_port_b_read  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int data=0;
		switch(pet_keyline_select) {
		case 0: data|=pet_keyline[0];break;
		case 1: data|=pet_keyline[1];break;
		case 2: data|=pet_keyline[2];break;
		case 3: data|=pet_keyline[3];break;
		case 4: data|=pet_keyline[4];break;
		case 5: data|=pet_keyline[5];break;
		case 6: data|=pet_keyline[6];break;
		case 7: data|=pet_keyline[7];break;
		case 8: data|=pet_keyline[8];break;
		case 9: data|=pet_keyline[9];break;
		}
		return data^0xff;
	} };
	
	public static WriteHandlerPtr pet_pia0_ca2_out = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cbm_ieee_eoi_w(0, data);
	} };
	
	static void pet_irq (int level)
	{
		static int old_level = 0;
	
		if (level != old_level)
		{
			DBG_LOG (3, "mos6502", ("irq %s\n", level ? "start" : "end"));
			if (superpet != 0)
				cpu_set_irq_line (1, M6809_IRQ_LINE, level);
			cpu_set_irq_line (0, M6502_INT_IRQ, level);
			old_level = level;
		}
	}
	
	/* pia at 0xe820 (ieee488)
	   port a data in
	   port b data out
	  ca1 atn in
	  ca2 ndac out
	  cb1 srq in
	  cb2 dav out
	 */
	public static ReadHandlerPtr pet_pia1_port_a_read  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cbm_ieee_data_r();
	} };
	
	public static WriteHandlerPtr pet_pia1_port_b_write = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cbm_ieee_data_w(0, data);
	} };
	
	public static ReadHandlerPtr pet_pia1_ca1_read  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cbm_ieee_atn_r();
	} };
	
	public static WriteHandlerPtr pet_pia1_ca2_write = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cbm_ieee_ndac_w(0,data);
	} };
	
	public static WriteHandlerPtr pet_pia1_cb2_write = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cbm_ieee_dav_w(0,data);
	} };
	
	public static ReadHandlerPtr pet_pia1_cb1_read  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cbm_ieee_srq_r();
	} };
	
	static struct pia6821_interface pet_pia0={
	#if 0
		int (*in_a_func)(int offset);
		int (*in_b_func)(int offset);
		int (*in_ca1_func)(int offset);
		int (*in_cb1_func)(int offset);
		int (*in_ca2_func)(int offset);
		int (*in_cb2_func)(int offset);
		void (*out_a_func)(int offset, int val);
		void (*out_b_func)(int offset, int val);
		void (*out_ca2_func)(int offset, int val);
		void (*out_cb2_func)(int offset, int val);
		void (*irq_a_func)(int state);
		void (*irq_b_func)(int state);
	#endif
		pet_pia0_port_a_read,
		pet_pia0_port_b_read,
		NULL,
		NULL,
		NULL,
		NULL,
		pet_pia0_port_a_write,
		NULL,
		pet_pia0_ca2_out,
		NULL,
		NULL,
		pet_irq
	}, pet_pia1= {
		pet_pia1_port_a_read,
		NULL,
	    pet_pia1_ca1_read,
	    pet_pia1_cb1_read,
		NULL,
		NULL,
		NULL,
	    pet_pia1_port_b_write,
	    pet_pia1_ca2_write,
	    pet_pia1_cb2_write,
	};
	
	static void pet_address_line_11(int offset, int level)
	{
		DBG_LOG (1, "address line", ("%d\n", level));
		crtc6845_address_line_11(level);
	}
	
	/* userport, cassettes, rest ieee488
	   ca1 userport
	   ca2 character rom address line 11
	   pa user port
	
	   pb0 ieee ndac in
	   pb1 ieee nrfd out
	   pb2 ieee atn out
	   pb3 userport/cassettes
	   pb4 cassettes
	   pb5 userport/???
	   pb6 ieee nrfd in
	   pb7 ieee dav in
	
	   cb1 cassettes
	   cb2 user port
	 */
	static int pet_via_port_b_r(int offset)
	{
		UINT8 data=0;
		if (cbm_ieee_ndac_r()) data|=1;
		if (cbm_ieee_nrfd_r()) data|=0x40;
		if (cbm_ieee_dav_r()) data|=0x80;
		return data;
	}
	
	static void pet_via_port_b_w(int offset, int data)
	{
		cbm_ieee_nrfd_w(0, data&2);
		cbm_ieee_atn_w(0, data&4);
	}
	
	
	static struct via6522_interface pet_via={
	#if 0
		int (*in_a_func)(int offset);
		int (*in_b_func)(int offset);
		int (*in_ca1_func)(int offset);
		int (*in_cb1_func)(int offset);
		int (*in_ca2_func)(int offset);
		int (*in_cb2_func)(int offset);
		void (*out_a_func)(int offset, int val);
		void (*out_b_func)(int offset, int val);
		void (*out_ca2_func)(int offset, int val);
		void (*out_cb2_func)(int offset, int val);
		void (*irq_func)(int state);
	
	    /* kludges for the Vectrex */
		void (*out_shift_func)(int val);
		void (*t2_callback)(double time);
	#endif
		NULL,
		pet_via_port_b_r,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL,
		pet_via_port_b_w,
		pet_address_line_11
	};
	
	static struct {
		int bank; /* rambank to be switched in 0x9000 */
		int rom; /* rom socket 6502? at 0x9000 */
	} spet= { 0 };
	
	WRITE_HANDLER(cbm8096_io_w)
	{
		if (offset<0x10) ;
		else if (offset<0x14) pia_0_w(offset&3,data);
		else if (offset<0x20) ;
		else if (offset<0x24) pia_1_w(offset&3,data);
		else if (offset<0x40) ;
		else if (offset<0x50) via_0_w(offset&0xf,data);
		else if (offset<0x80) ;
		else if (offset<0x82) crtc6845_pet_port_w(offset&1,data);
	}
	
	extern READ_HANDLER(cbm8096_io_r)
	{
		int data=0xff;
		if (offset<0x10) ;
		else if (offset<0x14) data=pia_0_r(offset&3);
		else if (offset<0x20) ;
		else if (offset<0x24) data=pia_1_r(offset&3);
		else if (offset<0x40) ;
		else if (offset<0x50) data=via_0_r(offset&0xf);
		else if (offset<0x80) ;
		else if (offset<0x82) data=crtc6845_port_r(offset&1);
		return data;
	}
	
	/*
	65520        8096 memory control register
	        bit 0    1=write protect $8000-BFFF
	        bit 1    1=write protect $C000-FFFF
	        bit 2    $8000-BFFF bank select
	        bit 3    $C000-FFFF bank select
	        bit 5    1=screen peek through
	        bit 6    1=I/O peek through
	        bit 7    1=enable expansion memory
	
	*/
	WRITE_HANDLER(cbm8096_w)
	{
		if ((data & 0x80) != 0) {
			if ((data & 0x40) != 0) {
				cpu_setbankhandler_r(7, cbm8096_io_r);
				cpu_setbankhandler_w(7, cbm8096_io_w);
			} else {
				cpu_setbankhandler_r(7, MRA_BANK7);
				if (!(data&2)) {
					cpu_setbankhandler_w(7,MWA_BANK7);
				} else {
					cpu_setbankhandler_w(7,MWA_NOP);
				}
			}
			if (!(data&2)) {
				cpu_setbankhandler_w(6,MWA_BANK6);
				cpu_setbankhandler_w(8,MWA_BANK8);
				cpu_setbankhandler_w(9,MWA_BANK9);
			} else {
				cpu_setbankhandler_w(6,MWA_NOP);
				cpu_setbankhandler_w(8,MWA_NOP);
				cpu_setbankhandler_w(9,MWA_NOP);
			}
			if ((data & 0x20) != 0) {
				cpu_setbank(1,pet_memory+0x8000);
				cpu_setbankhandler_w(1, crtc6845_videoram_w);
			} else {
				if (!(data&1)) {
					cpu_setbankhandler_w(1,MWA_BANK1);
				} else {
					cpu_setbankhandler_w(1,MWA_NOP);
				}
			}
			if (!(data&1)) {
				cpu_setbankhandler_w(2,MWA_BANK2);
				cpu_setbankhandler_w(3,MWA_BANK3);
				cpu_setbankhandler_w(4,MWA_BANK4);
			} else {
				cpu_setbankhandler_w(2,MWA_NOP);
				cpu_setbankhandler_w(3,MWA_NOP);
				cpu_setbankhandler_w(4,MWA_NOP);
			}
			if ((data & 4) != 0) {
				if (!(data&0x20)) {
					cpu_setbank(1,pet_memory+0x14000);
				}
				cpu_setbank(2,pet_memory+0x15000);
				cpu_setbank(3,pet_memory+0x16000);
				cpu_setbank(4,pet_memory+0x17000);
			} else {
				if (!(data&0x20)) {
					cpu_setbank(1,pet_memory+0x10000);
				}
				cpu_setbank(2,pet_memory+0x11000);
				cpu_setbank(3,pet_memory+0x12000);
				cpu_setbank(4,pet_memory+0x13000);
			}
			if ((data & 8) != 0) {
				if (!(data&0x40)) {
					cpu_setbank(7,pet_memory+0x1e800);
				}
				cpu_setbank(6, pet_memory+0x1c000);
				cpu_setbank(8, pet_memory+0x1f000);
				cpu_setbank(9, pet_memory+0x1fff1);
			} else {
				if (!(data&0x40)) {
					cpu_setbank(7,pet_memory+0x1a800);
				}
				cpu_setbank(6, pet_memory+0x18000);
				cpu_setbank(8, pet_memory+0x1b000);
				cpu_setbank(9, pet_memory+0x1bff1);
			}
		} else {
			cpu_setbank(1,pet_memory+0x8000);
			cpu_setbankhandler_w(1, crtc6845_videoram_w);
			cpu_setbank(2,pet_memory+0x9000);
			cpu_setbankhandler_w(2, MWA_ROM);
			cpu_setbank(3,pet_memory+0xa000);
			cpu_setbankhandler_w(3, MWA_ROM);
			cpu_setbank(4,pet_memory+0xb000);
			cpu_setbankhandler_w(4, MWA_ROM);
	
			cpu_setbank(6,pet_memory+0xc000);
			cpu_setbankhandler_w(6, MWA_ROM);
			cpu_setbankhandler_r(7, cbm8096_io_r);
			cpu_setbankhandler_w(7, cbm8096_io_w);
			cpu_setbank(8,pet_memory+0xf000);
			cpu_setbankhandler_w(8, MWA_ROM);
			cpu_setbank(9,pet_memory+0xfff1);
			cpu_setbankhandler_w(9, MWA_ROM);
		}
	}
	
	extern READ_HANDLER(superpet_r)
	{
		int data=0xff;
		switch (offset) {
		}
		return data;
	}
	
	extern WRITE_HANDLER(superpet_w)
	{
		switch (offset) {
		case 6:case 7:
			spet.rom=data&1;
	
			break;
		case 4:case 5:
			spet.bank=data&0xf;
			cpu_setbank(3,superpet_memory+(spet.bank<<12));
			/* 7 low writeprotects systemlatch */
			break;
		case 0:case 1:case 2:case 3:
			/* 3: 1 pull down diagnostic pin on the userport
			   1: 1 if jumpered programable ram r/w
			   0: 0 if jumpered programable m6809, 1 m6502 selected */
			break;
		}
	}
	
	static void pet_common_driver_init (void)
	{
		int i;
		/* 2114 poweron ? 64 x 0xff, 64x 0, and so on */
		for (i=0; i<0x8000; i+=0x40) {
			memset (pet_memory + i, i & 0x40 ? 0 : 0xff, 0x40);
		}
		memset(pet_memory+0xe800, 0xff, 0x800);
	
		pet_clock=timer_pulse(0.01, 0, pet_frame_interrupt);
		via_config(0,&pet_via);
		pia_config(0,PIA_8BIT,&pet_pia0);
		pia_config(1,PIA_8BIT,&pet_pia1);
	
		cbm_drive_open ();
	
		cbm_drive_attach_fs (0);
		cbm_drive_attach_fs (1);
	
		cbm_ieee_open();
	}
	
	void pet_driver_init (void)
	{
		pet_common_driver_init ();
		raster2.display_state=pet_state;
		pet_init(pet_videoram);
	}
	
	void pet_basic1_driver_init (void)
	{
		pet_basic1=1;
		pet_common_driver_init ();
		raster2.display_state=pet_state;
		pet_init(pet_videoram);
	}
	
	void pet40_driver_init (void)
	{
		pet_common_driver_init ();
		raster2.display_state=pet_state;
		crtc6845_pet_init(pet_videoram);
	}
	
	void cbm80_driver_init (void)
	{
		cbm8096=1;
		pet_common_driver_init ();
		raster2.display_state=pet_state;
		crtc6845_pet_init(pet_videoram);
	}
	
	void superpet_driver_init(void)
	{
		superpet=1;
		pet_common_driver_init ();
	
		cpu_setbank(3, superpet_memory);
		memorycontextswap(1);
		cpu_setbank(1, pet_memory);
		cpu_setbank(2, pet_memory+0x8000);
		cpu_setbank(3, superpet_memory);
		memorycontextswap(0);
	
		raster2.display_state=pet_state;
		crtc6845_superpet_init(pet_videoram);
	}
	
	void pet_driver_shutdown (void)
	{
	}
	
	public static InitMachinePtr pet_init_machine = new InitMachinePtr() { public void handler() 
	{
		via_reset();
		pia_reset();
	
		if (superpet != 0) {
			spet.rom=0;
			if (M6809_SELECT != 0) {
				cpu_set_halt_line(0,1);
				cpu_set_halt_line(1,0);
			} else {
				cpu_set_halt_line(0,0);
				cpu_set_halt_line(1,1);
			}
		}
	
		switch (MEMORY) {
		case MEMORY_4:
			install_mem_write_handler (0, 0x1000, 0x1fff, MWA_NOP);
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_NOP);
			install_mem_write_handler (0, 0x4000, 0x7fff, MWA_NOP);
			break;
		case MEMORY_8:
			install_mem_write_handler (0, 0x1000, 0x1fff, MWA_RAM);
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_NOP);
			install_mem_write_handler (0, 0x4000, 0x7fff, MWA_NOP);
			break;
		case MEMORY_16:
			install_mem_write_handler (0, 0x1000, 0x1fff, MWA_RAM);
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_RAM);
			install_mem_write_handler (0, 0x4000, 0x7fff, MWA_NOP);
			break;
		case MEMORY_32:
			install_mem_write_handler (0, 0x1000, 0x1fff, MWA_RAM);
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_RAM);
			install_mem_write_handler (0, 0x4000, 0x7fff, MWA_RAM);
			break;
		}
	
		if (cbm8096 != 0) {
			if (CBM8096_MEMORY != 0) {
				install_mem_write_handler(0, 0xfff0, 0xfff0, cbm8096_w);
			} else {
				install_mem_write_handler(0, 0xfff0, 0xfff0, MWA_NOP);
			}
			cbm8096_w(0,0);
		}
	
		cbm_drive_0_config (IEEE8ON ? IEEE : 0, 8);
		cbm_drive_1_config (IEEE9ON ? IEEE : 0, 9);
	
		pet_rom_load();
	} };
	
	void pet_shutdown_machine (void)
	{
	}
	
	int pet_rom_id (int id)
	{
	#if 0
		/* magic lowrom at offset 0x8003: $c3 $c2 $cd $38 $30 */
		/* jumped to offset 0 (0x8000) */
		int retval = 0;
		unsigned char magic[] =
		{0xc3, 0xc2, 0xcd, 0x38, 0x30}, buffer[sizeof (magic)];
		FILE *romfile;
		char *cp;
	
		logerror("c64_rom_id %s\n", device_filename(IO_CARTSLOT,id));
		retval = 0;
		if (!(romfile = image_fopen (IO_CARTSLOT, id, OSD_FILETYPE_IMAGE_R, 0)))
		{
			logerror("rom %s not found\n", device_filename(IO_CARTSLOT,id));
			return 0;
		}
	
		osd_fseek (romfile, 3, SEEK_SET);
		osd_fread (romfile, buffer, sizeof (magic));
		osd_fclose (romfile);
	#endif
		return 1;
	}
	
	void pet_rom_load(void)
	{
		int i;
	
		for (i=0; (i<sizeof(cbm_rom)/sizeof(cbm_rom[0]))
				 &&(cbm_rom[i].size!=0); i++) {
			memcpy(pet_memory+cbm_rom[i].addr, cbm_rom[i].chip, cbm_rom[i].size);
		}
	}
	
	void pet_keyboard_business(void)
	{
		int value;
	
		value = 0;
		/*if (KEY_ESCAPE != 0) value|=0x80; // nothing, not blocking */
		/*if (KEY_REVERSE != 0) value|=0x40; // nothing, not blocking */
		if (KEY_B_CURSOR_RIGHT != 0) value |= 0x20;
		if (KEY_B_PAD_8 != 0) value |= 0x10;
		if (KEY_B_MINUS != 0) value |= 8;
		if (KEY_B_8 != 0) value |= 4;
		if (KEY_B_5 != 0) value |= 2;
		if (KEY_B_2 != 0) value |= 1;
		pet_keyline[0] = value;
	
		value = 0;
		if (KEY_B_PAD_9 != 0) value |= 0x80;
		/*if (KEY_ESCAPE != 0) value|=0x40; // nothing, not blocking */
		if (KEY_B_ARROW_UP != 0) value |= 0x20;
		if (KEY_B_PAD_7 != 0) value |= 0x10;
		if (KEY_B_PAD_0 != 0) value |= 8;
		if (KEY_B_7 != 0) value |= 4;
		if (KEY_B_4 != 0) value |= 2;
		if (KEY_B_1 != 0) value |= 1;
		pet_keyline[1] = value;
	
		value = 0;
		if (KEY_B_PAD_5 != 0) value |= 0x80;
		if (KEY_B_SEMICOLON != 0) value |= 0x40;
		if (KEY_B_K != 0) value |= 0x20;
		if (KEY_B_CLOSEBRACE != 0) value |= 0x10;
		if (KEY_B_H != 0) value |= 8;
		if (KEY_B_F != 0) value |= 4;
		if (KEY_B_S != 0) value |= 2;
		if (KEY_B_ESCAPE != 0) value |= 1; /* upper case */
		pet_keyline[2] = value;
	
		value = 0;
		if (KEY_B_PAD_6 != 0) value |= 0x80;
		if (KEY_B_ATSIGN != 0) value |= 0x40;
		if (KEY_B_L != 0) value |= 0x20;
		if (KEY_B_RETURN != 0) value |= 0x10;
		if (KEY_B_J != 0) value |= 8;
		if (KEY_B_G != 0) value |= 4;
		if (KEY_B_D != 0) value |= 2;
		if (KEY_B_A != 0) value |= 1;
		pet_keyline[3] = value;
	
		value = 0;
		if (KEY_B_DEL != 0) value |= 0x80;
		if (KEY_B_P != 0) value |= 0x40;
		if (KEY_B_I != 0) value |= 0x20;
		if (KEY_B_BACKSLASH != 0) value |= 0x10;
		if (KEY_B_Y != 0) value |= 8;
		if (KEY_B_R != 0) value |= 4;
		if (KEY_B_W != 0) value |= 2;
		if (KEY_B_TAB != 0) value |= 1;
		pet_keyline[4] = value;
	
		value = 0;
		if (KEY_B_PAD_4 != 0) value |= 0x80;
		if (KEY_B_OPENBRACE != 0) value |= 0x40;
		if (KEY_B_O != 0) value |= 0x20;
		if (KEY_B_CURSOR_DOWN != 0) value |= 0x10;
		if (KEY_B_U != 0) value |= 8;
		if (KEY_B_T != 0) value |= 4;
		if (KEY_B_E != 0) value |= 2;
		if (KEY_B_Q != 0) value |= 1;
		pet_keyline[5] = value;
	
		value = 0;
		if (KEY_B_PAD_3 != 0) value |= 0x80;
		if (KEY_B_RIGHT_SHIFT != 0) value |= 0x40;
		/*if (KEY_REVERSE != 0) value |= 0x20; // clear line */
		if (KEY_B_PAD_POINT != 0) value |= 0x10;
		if (KEY_B_POINT != 0) value |= 8;
		if (KEY_B_B != 0) value |= 4;
		if (KEY_B_C != 0) value |= 2;
		if (KEY_B_LEFT_SHIFT != 0) value |= 1;
		pet_keyline[6] = value;
	
		value = 0;
		if (KEY_B_PAD_2 != 0) value |= 0x80;
		if (KEY_B_REPEAT != 0) value |= 0x40;
		/*if (KEY_REVERSE != 0) value |= 0x20; // blocking */
		if (KEY_B_0 != 0) value |= 0x10;
		if (KEY_B_COMMA != 0) value |= 8;
		if (KEY_B_N != 0) value |= 4;
		if (KEY_B_V != 0) value |= 2;
		if (KEY_B_Z != 0) value |= 1;
		pet_keyline[7] = value;
	
		value = 0;
		if (KEY_B_PAD_1 != 0) value |= 0x80;
		if (KEY_B_SLASH != 0) value |= 0x40;
		/*if (KEY_ESCAPE != 0) value |= 0x20; // special clear */
		if (KEY_B_HOME != 0) value |= 0x10;
		if (KEY_B_M != 0) value |= 8;
		if (KEY_B_SPACE != 0) value |= 4;
		if (KEY_B_X != 0) value |= 2;
		if (KEY_B_REVERSE != 0) value |= 1; /* lower case */
		pet_keyline[8] = value;
	
		value = 0;
		/*if (KEY_ESCAPE != 0) value |= 0x80; // blocking */
		/*if (KEY_REVERSE != 0) value |= 0x40; // blocking */
		if (KEY_B_COLON != 0) value |= 0x20;
		if (KEY_B_STOP != 0) value |= 0x10;
		if (KEY_B_9 != 0) value |= 8;
		if (KEY_B_6 != 0) value |= 4;
		if (KEY_B_3 != 0) value |= 2;
		if (KEY_B_ARROW_LEFT != 0) value |= 1;
		pet_keyline[9] = value;
	}
	
	void pet_keyboard_normal(void)
	{
		int value;
		value = 0;
		if (KEY_CURSOR_RIGHT != 0) value |= 0x80;
		if (KEY_HOME != 0) value |= 0x40;
		if (KEY_ARROW_LEFT != 0) value |= 0x20;
		if (KEY_9 != 0) value |= 0x10;
		if (KEY_7 != 0) value |= 8;
		if (KEY_5 != 0) value |= 4;
		if (KEY_3 != 0) value |= 2;
		if (KEY_1 != 0) value |= 1;
		pet_keyline[0] = value;
	
		value = 0;
		if (KEY_PAD_DEL != 0) value |= 0x80;
		if (KEY_CURSOR_DOWN != 0) value |= 0x40;
		/*if (KEY_3 != 0) value |= 0x20; // not blocking */
		if (KEY_0 != 0) value |= 0x10;
		if (KEY_8 != 0) value |= 8;
		if (KEY_6 != 0) value |= 4;
		if (KEY_4 != 0) value |= 2;
		if (KEY_2 != 0) value |= 1;
		pet_keyline[1] = value;
	
		value = 0;
		if (KEY_PAD_9 != 0)
			value |= 0x80;
		if (KEY_PAD_7 != 0)
			value |= 0x40;
		if (KEY_ARROW_UP != 0) value |= 0x20;
		if (KEY_O != 0)
			value |= 0x10;
		if (KEY_U != 0)
			value |= 8;
		if (KEY_T != 0)
			value |= 4;
		if (KEY_E != 0)
			value |= 2;
		if (KEY_Q != 0)
			value |= 1;
		pet_keyline[2] = value;
	
		value = 0;
		if (KEY_PAD_SLASH != 0) value |= 0x80;
		if (KEY_PAD_8 != 0)
			value |= 0x40;
		/*if (KEY_5 != 0) value |= 0x20; // not blocking */
		if (KEY_P != 0)
			value |= 0x10;
		if (KEY_I != 0)
			value |= 8;
		if (KEY_Y != 0)
			value |= 4;
		if (KEY_R != 0)
			value |= 2;
		if (KEY_W != 0)
			value |= 1;
		pet_keyline[3] = value;
	
		value = 0;
		if (KEY_PAD_6 != 0)
			value |= 0x80;
		if (KEY_PAD_4 != 0)
			value |= 0x40;
		/* if (KEY_6 != 0) value |= 0x20; // not blocking */
		if (KEY_L != 0)
			value |= 0x10;
		if (KEY_J != 0)
			value |= 8;
		if (KEY_G != 0)
			value |= 4;
		if (KEY_D != 0)
			value |= 2;
		if (KEY_A != 0)
			value |= 1;
		pet_keyline[4] = value;
	
		value = 0;
		if (KEY_PAD_ASTERIX != 0) value |= 0x80;
		if (KEY_PAD_5 != 0)
			value |= 0x40;
		/*if (KEY_8 != 0) value |= 0x20; // not blocking */
		if (KEY_COLON != 0) value |= 0x10;
		if (KEY_K != 0)
			value |= 8;
		if (KEY_H != 0)
			value |= 4;
		if (KEY_F != 0)
			value |= 2;
		if (KEY_S != 0)
			value |= 1;
		pet_keyline[5] = value;
	
		value = 0;
		if (KEY_PAD_3 != 0)
			value |= 0x80;
		if (KEY_PAD_1 != 0) value |= 0x40;
		if (KEY_RETURN != 0)
			value |= 0x20;
		if (KEY_SEMICOLON != 0)
			value |= 0x10;
		if (KEY_M != 0)
			value |= 8;
		if (KEY_B != 0)
			value |= 4;
		if (KEY_C != 0)
			value |= 2;
		if (KEY_Z != 0)
			value |= 1;
		pet_keyline[6] = value;
	
		value = 0;
		if (KEY_PAD_PLUS != 0) value |= 0x80;
		if (KEY_PAD_2 != 0)
			value |= 0x40;
		/*if (KEY_2 != 0) value |= 0x20; // non blocking */
		if (KEY_QUESTIONMARK != 0) value |= 0x10;
		if (KEY_COMMA != 0)
			value |= 8;
		if (KEY_N != 0)
			value |= 4;
		if (KEY_V != 0)
			value |= 2;
		if (KEY_X != 0)
			value |= 1;
		pet_keyline[7] = value;
	
		value = 0;
		if (KEY_PAD_MINUS != 0)
			value |= 0x80;
		if (KEY_PAD_0 != 0) value |= 0x40;
	    if (KEY_RIGHT_SHIFT != 0) value |= 0x20;
		if (KEY_BIGGER != 0) value |= 0x10;
		/*if (KEY_5 != 0) value |= 8; // non blocking */
		if (KEY_CLOSEBRACE != 0) value |= 4;
		if (KEY_ATSIGN != 0) value |= 2;
		if (KEY_LEFT_SHIFT != 0) value |= 1;
		pet_keyline[8] = value;
	
		value = 0;
		if (KEY_PAD_EQUALS != 0) value |= 0x80;
		if (KEY_PAD_POINT != 0)
			value |= 0x40;
		/*if (KEY_6 != 0) value |= 0x20; // non blocking */
		if (KEY_STOP != 0) value |= 0x10;
		if (KEY_SMALLER != 0) value |= 8;
		if (KEY_SPACE != 0) value |= 4;
		if (KEY_OPENBRACE != 0)
			value |= 2;
		if (KEY_REVERSE != 0) value |= 1;
		pet_keyline[9] = value;
	}
	
	void pet_frame_interrupt (int param)
	{
		static int level=0;
		static int quickload = 0;
	
		pia_0_cb1_w(0,level);
		level=!level;
		if (level != 0) return;
	
		if (superpet != 0) {
			if (M6809_SELECT != 0) {
				cpu_set_halt_line(0,1);
				cpu_set_halt_line(1,0);
				crtc6845_address_line_12(1);
			} else {
				cpu_set_halt_line(0,0);
				cpu_set_halt_line(1,1);
				crtc6845_address_line_12(0);
			}
		}
	
		if (BUSINESS_KEYBOARD != 0) {
			pet_keyboard_business();
		} else {
			pet_keyboard_normal();
		}
	
		if (!quickload && QUICKLOAD) {
			if (pet_basic1 != 0)
				cbm_pet1_quick_open (0, 0, pet_memory);
			else
				cbm_pet_quick_open (0, 0, pet_memory);
		}
		quickload = QUICKLOAD;
	
		osd_led_w (1 /*KB_CAPSLOCK_FLAG */ , KEY_B_SHIFTLOCK ? 1 : 0);
	}
	
	void pet_state(PRASTER *This)
	{
		int y;
		char text[70];
	
		y = Machine.visible_area.max_y + 1 - Machine.uifont.height;
	
	#if 0
		snprintf(text, sizeof(text),
				 "%.2x %.2x %.2x %.2x %.2x %.2x %.2x %.2x %.2x %.2x",
				 pet_keyline[0],
				 pet_keyline[1],
				 pet_keyline[2],
				 pet_keyline[3],
				 pet_keyline[4],
				 pet_keyline[5],
				 pet_keyline[6],
				 pet_keyline[7],
				 pet_keyline[8],
				 pet_keyline[9]);
		praster_draw_text (This, text, &y);
	#endif
	
		cbm_drive_0_status (text, sizeof (text));
		praster_draw_text (This, text, &y);
	
		cbm_drive_1_status (text, sizeof (text));
		praster_draw_text (This, text, &y);
	}
}
