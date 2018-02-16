/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class pocketc
{
	
	
	/* C-CE while reset, program will not be destroyed! */
	
	/* error codes
	1 syntax error
	2 calculation error
	3 illegal function argument
	4 too large a line number
	5 next without for
	  return without gosub
	6 memory overflow
	7 print using error
	8 i/o device error
	9 other errors*/
	
	static UINT8 outa,outb;
	
	static int power=1; /* simulates pressed cce when mess is started */
	
	void pc1401_outa(int data)
	{
		outa=data;
	}
	
	void pc1401_outb(int data)
	{
		outb=data;
	}
	
	void pc1401_outc(int data)
	{
	
	}
	
	int pc1401_ina(void)
	{
		int data=outa;
		if ((outb & 1) != 0) {
			if (KEY_SIGN != 0) data|=1;
			if (KEY_8 != 0) data|=2;
			if (KEY_2 != 0) data|=4;
			if (KEY_5 != 0) data|=8;
			if (KEY_CAL != 0) data|=0x10;
			if (KEY_Q != 0) data|=0x20;
			if (KEY_A != 0) data|=0x40;
			if (KEY_Z != 0) data|=0x80;
		}
		if ((outb & 2) != 0) {
			if (KEY_POINT != 0) data|=1;
			if (KEY_9 != 0) data|=2;
			if (KEY_3 != 0) data|=4;
			if (KEY_6 != 0) data|=8;
			if (KEY_BASIC != 0) data|=0x10;
			if (KEY_W != 0) data|=0x20;
			if (KEY_S != 0) data|=0x40;
			if (KEY_X != 0) data|=0x80;
		}
		if ((outb & 4) != 0) {
			if (KEY_PLUS != 0) data|=1;
			if (KEY_DIV != 0) data|=2;
			if (KEY_MINUS != 0) data|=4;
			if (KEY_MUL != 0) data|=8;
			if (KEY_DEF != 0) data|=0x10;
			if (KEY_E != 0) data|=0x20;
			if (KEY_D != 0) data|=0x40;
			if (KEY_C != 0) data|=0x80;
		}
		if ((outb & 8) != 0) {
			if (KEY_BRACE_RIGHT != 0) data|=1;
			if (KEY_BRACE_LEFT != 0) data|=2;
			if (KEY_SQUARE != 0) data|=4;
			if (KEY_ROOT != 0) data|=8;
			if (KEY_POT != 0) data|=0x10;
			if (KEY_EXP != 0) data|=0x20;
			if (KEY_XM != 0) data|=0x40;
			if (KEY_EQUALS != 0) data|=0x80;
		}
		if ((outb & 0x10) != 0) {
			if (KEY_STAT != 0) data|=1;
			if (KEY_1X != 0) data|=2;
			if (KEY_LOG != 0) data|=4;
			if (KEY_LN != 0) data|=8;
			if (KEY_DEG != 0) data|=0x10;
			if (KEY_HEX != 0) data|=0x20;
			if (KEY_MPLUS != 0) data|=0x80;
		}
		if ((outb & 0x20) != 0) {
			if (power||(KEY_CCE)) data|=1;
			if (KEY_FE != 0) data|=2;
			if (KEY_TAN != 0) data|=4;
			if (KEY_COS != 0) data|=8;
			if (KEY_SIN != 0) data|=0x10;
			if (KEY_HYP != 0) data|=0x20;
			if (KEY_SHIFT != 0) data|=0x40;
			if (KEY_RM != 0) data|=0x80;
		}
		if ((outa & 1) != 0) {
			if (KEY_7 != 0) data|=2;
			if (KEY_1 != 0) data|=4;
			if (KEY_4 != 0) data|=8;
			if (KEY_DOWN != 0) data|=0x10;
			if (KEY_R != 0) data|=0x20;
			if (KEY_F != 0) data|=0x40;
			if (KEY_V != 0) data|=0x80;
		}
		if ((outa & 2) != 0) {
			if (KEY_COMMA != 0) data|=4;
			if (KEY_P != 0) data|=8;
			if (KEY_UP != 0) data|=0x10;
			if (KEY_T != 0) data|=0x20;
			if (KEY_G != 0) data|=0x40;
			if (KEY_B != 0) data|=0x80;
		}
		if ((outa & 4) != 0) {
			if (KEY_O != 0) data|=8;
			if (KEY_LEFT != 0) data|=0x10;
			if (KEY_Y != 0) data|=0x20;
			if (KEY_H != 0) data|=0x40;
			if (KEY_N != 0) data|=0x80;
		}
		if ((outa & 8) != 0) {
			if (KEY_RIGHT != 0) data|=0x10;
			if (KEY_U != 0) data|=0x20;
			if (KEY_J != 0) data|=0x40;
			if (KEY_M != 0) data|=0x80;
		}
		if ((outa & 0x10) != 0) {
			if (KEY_I != 0) data|=0x20;
			if (KEY_K != 0) data|=0x40;
			if (KEY_SPC != 0) data|=0x80;
		}
		if ((outa & 0x20) != 0) {
			if (KEY_L != 0) data|=0x40;
			if (KEY_ENTER != 0) data|=0x80;
		}
		if ((outa & 0x40) != 0) {
			if (KEY_0 != 0) data|=0x80;
		}
		return data;
	}
	
	int pc1251_ina(void)
	{
		int data=outa;
	
		if ((outb & 1) != 0) {
			if (PC1251_KEY_MINUS != 0) data|=1;
			if (power||PC1251_KEY_CL) data|=2; // problem with the deg lcd
			if (PC1251_KEY_ASTERIX != 0) data|=4;
			if (PC1251_KEY_SLASH != 0) data|=8;
			if (PC1251_KEY_DOWN != 0) data|=0x10;
			if (PC1251_KEY_E != 0) data|=0x20;
			if (PC1251_KEY_D != 0) data|=0x40;
			if (PC1251_KEY_C != 0) data|=0x80;
		}
		if ((outb & 2) != 0) {
			if (PC1251_KEY_PLUS != 0) data|=1;
			if (PC1251_KEY_9 != 0) data|=2;
			if (PC1251_KEY_3 != 0) data|=4;
			if (PC1251_KEY_6 != 0) data|=8;
			if (PC1251_KEY_SHIFT != 0) data|=0x10;
			if (PC1251_KEY_W != 0) data|=0x20;
			if (PC1251_KEY_S != 0) data|=0x40;
			if (PC1251_KEY_X != 0) data|=0x80;
		}
		if ((outb & 4) != 0) {
			if (PC1251_KEY_POINT != 0) data|=1;
			if (PC1251_KEY_8 != 0) data|=2;
			if (PC1251_KEY_2 != 0) data|=4;
			if (PC1251_KEY_5 != 0) data|=8;
			if (PC1251_KEY_DEF != 0) data|=0x10;
			if (PC1251_KEY_Q != 0) data|=0x20;
			if (PC1251_KEY_A != 0) data|=0x40;
			if (PC1251_KEY_Z != 0) data|=0x80;
		}
		if ((outa & 1) != 0) {
			if (PC1251_KEY_7 != 0) data|=2;
			if (PC1251_KEY_1 != 0) data|=4;
			if (PC1251_KEY_4 != 0) data|=8;
			if (PC1251_KEY_UP != 0) data|=0x10;
			if (PC1251_KEY_R != 0) data|=0x20;
			if (PC1251_KEY_F != 0) data|=0x40;
			if (PC1251_KEY_V != 0) data|=0x80;
		}
		if ((outa & 2) != 0) {
			if (PC1251_KEY_EQUALS != 0) data|=4;
			if (PC1251_KEY_P != 0) data|=8;
			if (PC1251_KEY_LEFT != 0) data|=0x10;
			if (PC1251_KEY_T != 0) data|=0x20;
			if (PC1251_KEY_G != 0) data|=0x40;
			if (PC1251_KEY_B != 0) data|=0x80;
		}
		if ((outa & 4) != 0) {
			if (PC1251_KEY_O != 0) data|=8;
			if (PC1251_KEY_RIGHT != 0) data|=0x10;
			if (PC1251_KEY_Y != 0) data|=0x20;
			if (PC1251_KEY_H != 0) data|=0x40;
			if (PC1251_KEY_N != 0) data|=0x80;
		}
		if ((outa & 8) != 0) {
	//		if (PC1251_KEY_DOWN != 0) data|=0x10; //?
			if (PC1251_KEY_U != 0) data|=0x20;
			if (PC1251_KEY_J != 0) data|=0x40;
			if (PC1251_KEY_M != 0) data|=0x80;
		}
		if ((outa & 0x10) != 0) {
			if (PC1251_KEY_I != 0) data|=0x20;
			if (PC1251_KEY_K != 0) data|=0x40;
			if (PC1251_KEY_SPACE != 0) data|=0x80;
		}
		if ((outa & 0x20) != 0) {
			if (PC1251_KEY_L != 0) data|=0x40;
			if (PC1251_KEY_ENTER != 0) data|=0x80;
		}
		if ((outa & 0x40) != 0) {
			if (PC1251_KEY_0 != 0) data|=0x80;
		}
	
		return data;
	}
	
	int pc1350_ina(void)
	{
		int data=outa;
		int t=pc1350_keyboard_line_r();
		if ((t & 1) != 0) {
			if (PC1350_KEY_BRACE_CLOSE != 0) data|=1;
			if (PC1350_KEY_COLON != 0) data|=2;
			if (PC1350_KEY_SEMICOLON != 0) data|=4;
			if (PC1350_KEY_COMMA != 0) data|=8;
			if (PC1350_KEY_SML != 0) data|=0x10;
			if (PC1350_KEY_DEF != 0) data|=0x20;
			// not sure if both shifts are wired to the same contacts
			if (PC1350_KEY_LSHIFT||PC1350_KEY_RSHIFT) data|=0x40;
		}
		if ((t & 2) != 0) {
			if (PC1350_KEY_BRACE_OPEN != 0) data|=1;
			if (PC1350_KEY_SLASH != 0) data|=2;
			if (PC1350_KEY_ASTERIX != 0) data|=4;
			if (PC1350_KEY_MINUS != 0) data|=8;
			if (PC1350_KEY_Z != 0) data|=0x10;
			if (PC1350_KEY_A != 0) data|=0x20;
			if (PC1350_KEY_Q != 0) data|=0x40;
		}
		if ((t & 4) != 0) {
			if (PC1350_KEY_9 != 0) data|=1;
			if (PC1350_KEY_6 != 0) data|=2;
			if (PC1350_KEY_3 != 0) data|=4;
			if (PC1350_KEY_PLUS != 0) data|=8;
			if (PC1350_KEY_X != 0) data|=0x10;
			if (PC1350_KEY_S != 0) data|=0x20;
			if (PC1350_KEY_W != 0) data|=0x40;
		}
		if ((t & 8) != 0) {
			if (PC1350_KEY_8 != 0) data|=1;
			if (PC1350_KEY_5 != 0) data|=2;
			if (PC1350_KEY_2 != 0) data|=4;
			if (PC1350_KEY_POINT != 0) data|=8;
			if (PC1350_KEY_C != 0) data|=0x10;
			if (PC1350_KEY_D != 0) data|=0x20;
			if (PC1350_KEY_E != 0) data|=0x40;
		}
		if ((t & 0x10) != 0) {
			if (PC1350_KEY_7 != 0) data|=1;
			if (PC1350_KEY_4 != 0) data|=2;
			if (PC1350_KEY_1 != 0) data|=4;
			if (PC1350_KEY_0 != 0) data|=8;
			if (PC1350_KEY_V != 0) data|=0x10;
			if (PC1350_KEY_F != 0) data|=0x20;
			if (PC1350_KEY_R != 0) data|=0x40;
		}
		if ((t & 0x20) != 0) {
			if (PC1350_KEY_UP != 0) data|=1;
			if (PC1350_KEY_DOWN != 0) data|=2;
			if (PC1350_KEY_LEFT != 0) data|=4; 
			if (PC1350_KEY_RIGHT != 0) data|=8;
			if (PC1350_KEY_B != 0) data|=0x10;
			if (PC1350_KEY_G != 0) data|=0x20;
			if (PC1350_KEY_T != 0) data|=0x40;
		}
		if ((outa & 1) != 0) {
	//		if (PC1350_KEY_1 != 0) data|=2; //?
			if (PC1350_KEY_INS != 0) data|=4;
			if (PC1350_KEY_DEL != 0) data|=8;
			if (PC1350_KEY_N != 0) data|=0x10;
			if (PC1350_KEY_H != 0) data|=0x20;
			if (PC1350_KEY_Y != 0) data|=0x40;
		}
		if ((outa & 2) != 0) {
	//		if (PC1350_KEY_2 != 0) data|=4; //?
			if (PC1350_KEY_MODE != 0) data|=8;
			if (PC1350_KEY_M != 0) data|=0x10;
			if (PC1350_KEY_J != 0) data|=0x20;
			if (PC1350_KEY_U != 0) data|=0x40;
		}
		if ((outa & 4) != 0) {
			if (power||PC1350_KEY_CLS) data|=8;
			if (PC1350_KEY_SPACE != 0) data|=0x10;
			if (PC1350_KEY_K != 0) data|=0x20;
			if (PC1350_KEY_I != 0) data|=0x40;
		}
		if ((outa & 8) != 0) {
			if (PC1350_KEY_ENTER != 0) data|=0x10;
			if (PC1350_KEY_L != 0) data|=0x20;
			if (PC1350_KEY_O != 0) data|=0x40;
		}
		if ((outa & 0x10) != 0) {
			if (PC1350_KEY_EQUALS != 0) data|=0x20;
			if (PC1350_KEY_P != 0) data|=0x40;
		}
		if (PC1350_KEY_OFF&&(outa&0xc0) ) data|=0xc0;
	
		// missing lshift
		
		return data;
	}
	
	int pc1401_inb(void)
	{
		int data=outb;
		if (KEY_OFF != 0) data|=1;
		return data;
	}
	
	int pc1350_inb(void)
	{
		int data=outb;
		return data;
	}
	
	int pc1251_inb(void)
	{
		int data=outb;
		if ((outb & 8) != 0) data|=PC1251_SWITCH_MODE;
		return data;
	}
	
	int pc1401_brk(void)
	{
		return KEY_BRK;
	}
	
	int pc1251_brk(void)
	{
		return PC1251_KEY_BRK;
	}
	
	int pc1350_brk(void)
	{
		return PC1350_KEY_BRK;
	}
	
	int pc1401_reset(void)
	{
		return KEY_RESET;
	}
	
	/* currently enough to save the external ram */
	static void pc1401_load(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x2000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
									Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 0))==NULL) {
			power=0;
			return;
		}
	
		osd_fread(file, cpu, 96);
		osd_fread(file, ram, 0x2800);
		osd_fclose(file);
	}
	
	static void pc1401_save(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x2000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 1))==NULL)
			return;
	
		osd_fwrite(file, cpu, 96);
		osd_fwrite(file, ram, 0x2800);
		osd_fclose(file);
	}
	
	/* currently enough to save the external ram */
	static void pc1251_load(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x8000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 0))==NULL) {
			power=0;
			return;
		}
	
		osd_fread(file, cpu, 96);
		osd_fread(file, ram, 0x4800);
		osd_fclose(file);
	}
	
	static void pc1251_save(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x8000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 1))==NULL)
			return;
	
		osd_fwrite(file, cpu, 96);
		osd_fwrite(file, ram, 0x4800);
		osd_fclose(file);
	}
	
	/* currently enough to save the external ram */
	static void pc1350_load(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x2000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 0))==NULL) {
			power=0;
			return;
		}
	
		osd_fread(file, cpu, 96);
		osd_fread(file, ram, 0x5000);
		osd_fclose(file);
	}
	
	static void pc1350_save(void)
	{
		FILE *file;
		UINT8 *ram=memory_region(REGION_CPU1)+0x2000,
			*cpu=sc61860_internal_ram();
	
		if ( (file=(FILE*)osd_fopen(Machine.gamedrv.name,
							 Machine.gamedrv.name, OSD_FILETYPE_NVRAM, 1))==NULL)
			return;
	
		osd_fwrite(file, cpu, 96);
		osd_fwrite(file, ram, 0x5000);
		osd_fclose(file);
	}
	
	static void pc1401_power_up(int param)
	{
		power=0;
	}
	
	public static InitDriverPtr init_pc1401 = new InitDriverPtr() { public void handler() 
	{
		int i;
		UINT8 *gfx=memory_region(REGION_GFX1);
	#if 0
		char sucker[]={
			/* this routine dump the memory (start 0)
			   in an endless loop,
			   the pc side must be started before this
			   its here to allow verification of the decimal data
			   in mame disassembler
			*/
	#if 1
			18,4,/*lip xl */
			2,0,/*lia 0 startaddress low */
			219,/*exam */
			18,5,/*lip xh */
			2,0,/*lia 0 startaddress high */
			219,/*exam */
	/*400f x: */
			/* dump internal rom */
			18,5,/*lip 4 */
			89,/*ldm */
			218,/*exab */
			18,4,/*lip 5 */
			89,/*ldm */
			4,/*ix for increasing x */
			0,0,/*lii,0 */
			18,20,/*lip 20 */
			53, /* */
			18,20,/* lip 20 */
			219,/*exam */
	#else
			18,4,/*lip xl */
			2,255,/*lia 0 */
			219,/*exam */
			18,5,/*lip xh */
			2,255,/*lia 0 */
			219,/*exam */
	/*400f x: */
			/* dump external memory */
			4, /*ix */
			87,/*				 ldd */
	#endif
			218,/*exab */
	
	
	
			0,4,/*lii 4 */
	
			/*a: */
			218,/*				  exab */
			90,/*				  sl */
			218,/*				  exab */
			18,94,/*			lip 94 */
			96,252,/*				  anma 252 */
			2,2, /*lia 2 */
			196,/*				  adcm */
			95,/*				  outf */
			/*b:  */
			204,/*inb */
			102,128,/*tsia 0x80 */
	#if 0
			41,4,/*			   jnzm b */
	#else
			/* input not working reliable! */
			/* so handshake removed, PC side must run with disabled */
			/* interrupt to not lose data */
			78,20, /*wait 20 */
	#endif
	
			218,/*				  exab */
			90,/*				  sl */
			218,/*				  exab */
			18,94,/*			lip 94 */
			96,252,/*anma 252 */
			2,0,/*				  lia 0 */
			196,/*adcm */
			95,/*				  outf */
			/*c:  */
			204,/*inb */
			102,128,/*tsia 0x80 */
	#if 0
			57,4,/*			   jzm c */
	#else
			78,20, /*wait 20 */
	#endif
	
			65,/*deci */
			41,34,/*jnzm a */
	
			41,41,/*jnzm x: */
	
			55,/*				rtn */
		};
	
		for (i=0; i<sizeof(sucker);i++) pc1401_mem[0x4000+i]=sucker[i];
		printf("%d %d\n",i, 0x4000+i);
	#endif
		for (i=0; i<128; i++) gfx[i]=i;
	
		pc1401_load();
		timer_pulse(1/500.0, 0,sc61860_2ms_tick);
		timer_set(1,0,pc1401_power_up);
	} };
	
	public static InitDriverPtr init_pc1251 = new InitDriverPtr() { public void handler() 
	{
		int i;
		UINT8 *gfx=memory_region(REGION_GFX1);
		for (i=0; i<128; i++) gfx[i]=i;
	
		pc1251_load();
		timer_pulse(1/500.0, 0,sc61860_2ms_tick);
		timer_set(1,0,pc1401_power_up);
	} };
	
	
	public static InitDriverPtr init_pc1350 = new InitDriverPtr() { public void handler() 
	{
		int i;
		UINT8 *gfx=memory_region(REGION_GFX1);
		for (i=0; i<256; i++) gfx[i]=i;
	
		pc1350_load();
		timer_pulse(1/500.0, 0,sc61860_2ms_tick);
		timer_set(1,0,pc1401_power_up);
	} };
	
	public static InitMachinePtr pc1401_machine_init = new InitMachinePtr() { public void handler() 
	{
		if (RAM10K != 0) {
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_RAM);
		} else if (RAM4K != 0) {
			install_mem_write_handler (0, 0x2000, 0x27ff, MWA_NOP);
			install_mem_write_handler (0, 0x3800, 0x3fff, MWA_RAM);
		} else {
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_NOP);
		}
	} };
	
	public static InitMachinePtr pc1251_machine_init = new InitMachinePtr() { public void handler() 
	{
		// c600 b800 b000 a000 8000 tested	
		// 4 kb memory feedback 512 bytes too few???
		// 11 kb ram: program stored at 8000
	#if 1
		install_mem_write_handler (0, 0x8000, 0xc7ff, MWA_RAM);
	#else
		if (PC1251_RAM11K != 0) {
			install_mem_write_handler (0, 0x8000, 0xafff, MWA_RAM);
			install_mem_write_handler (0, 0xb000, 0xc5ff, MWA_NOP);
			install_mem_write_handler (0, 0xc600, 0xc7ff, MWA_RAM);
			install_mem_write_handler (0, 0xc800, 0xf7ff, MWA_RAM);
		} else if (PC1251_RAM6K != 0) {
			install_mem_write_handler (0, 0xa000, 0xafff, MWA_NOP);
			install_mem_write_handler (0, 0xb000, 0xc7ff, MWA_RAM);
			install_mem_write_handler (0, 0xc800, 0xcbff, MWA_RAM);
		} else if (PC1251_RAM4K != 0) {
			install_mem_write_handler (0, 0xa000, 0xb7ff, MWA_NOP);
			install_mem_write_handler (0, 0xb800, 0xc7ff, MWA_RAM);
			install_mem_write_handler (0, 0xc800, 0xcbff, MWA_RAM);
		} else {
			install_mem_write_handler (0, 0xa000, 0xc5ff, MWA_NOP);
			install_mem_write_handler (0, 0xc600, 0xc9ff, MWA_RAM);
			install_mem_write_handler (0, 0xca00, 0xcbff, MWA_RAM);
		}
	#endif
	} };
	
	public static InitMachinePtr pc1350_machine_init = new InitMachinePtr() { public void handler() 
	{
		if (PC1350_RAM20K != 0) {
			install_mem_write_handler (0, 0x2000, 0x5fff, MWA_RAM);
		} else if (PC1350_RAM12K != 0) {
			install_mem_write_handler (0, 0x2000, 0x3fff, MWA_NOP);
			install_mem_write_handler (0, 0x4000, 0x5fff, MWA_RAM);
		} else {
			install_mem_write_handler (0, 0x2000, 0x5fff, MWA_NOP);
		}
	} };
	
	void pc1401_machine_stop(void)
	{
		pc1401_save();
	}
	
	void pc1251_machine_stop(void)
	{
		pc1251_save();
	}
	
	void pc1350_machine_stop(void)
	{
		pc1350_save();
	}
}
