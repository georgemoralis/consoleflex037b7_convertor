/***************************************************************************

  machine.c

  Functions to emulate general aspects of the machine (RAM, ROM, interrupts,
  I/O ports)

  Using a 6821 instead of the 6820.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class apple1
{
	
	struct pia6821_interface apple1_pia0 =
	{
		apple1_pia0_kbdin,				   /* returns key input */
		apple1_pia0_dsprdy,				   /* Bit 7 low when display ready */
		apple1_pia0_kbdrdy,				   /* Bit 7 high means key pressed */
		0,								   /* CA2 not used */
		0,								   /* CB1 not used */
		0,								   /* CB2 not used */
		0,								   /* CRA has no output */
		apple1_pia0_dspout,				   /* Send character to screen */
		0,								   /* CA2 not written to */
		0,								   /* CB2 not written to */
		0,								   /* Interrupts not connected */
		0								   /* Interrupts not connected */
	};
	
	static UINT8 apple1_kbd_conv[] =
	{
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', '-', '=', '[', ']', ';', '\'',
		'\\', ',', '.', '/', '#', 'A', 'B', 'C',
		'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
		'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
		'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 0x0d,
		0x5f, ' ', 0x1b,
	/* shifted */
		')', '!', '"', '3', '$', '%', '^', '&',
		'*', '(', '-', '+', '[', ']', ':', '@',
		'\\', '<', '>', '?', '#', 'A', 'B', 'C',
		'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
		'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
		'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 0x0d,
		0x5f, ' ', 0x1b
	};
	
	static int apple1_kbd_data;
	
	public static InitMachinePtr apple1_init_machine = new InitMachinePtr() { public void handler() 
	{
		logerror("apple1_init\r\n");
	
		if (readinputport(4) & 0x01)
		{
			install_mem_write_handler (0, 0x2000, 0xcfff, MWA_RAM);
			install_mem_read_handler (0, 0x2000, 0xcfff, MRA_RAM);
		}
		else
		{
			install_mem_write_handler (0, 0x2000, 0xcfff, MWA_NOP);
			install_mem_read_handler (0, 0x2000, 0xcfff, MRA_NOP);
		}
		pia_config(0, PIA_8BIT | PIA_AUTOSENSE, &apple1_pia0);
	} };
	
	void apple1_stop_machine(void)
	{
	}
	
	public static InterruptPtr apple1_interrupt = new InterruptPtr() { public int handler() 
	{
		int loop;
	
	/* Check for keypresses */
	
		apple1_kbd_data = 0;
		if (readinputport(3) & 0x0020)	/* Reset */
		{
			for (loop = 0; loop < 0xcfff; loop++) cpu_writemem16(loop, 0);
			apple1_vh_dsp_clr();
			pia_reset();
			m6502_reset(NULL);
			m6502_set_pc(0xff00);
		}
		else if (readinputport(3) & 0x0040)	/* clear screen */
		{
			apple1_vh_dsp_clr();
		}
		else
		{
			for (loop = 0; loop < 51; loop++)
			{
				if (readinputport(loop / 16) & (1 << (loop & 15)))
				{
					if (readinputport(3) & 0x0018)	/* shift keys */
					{
						apple1_kbd_data = apple1_kbd_conv[loop + 51];
					}
					else
					{
						apple1_kbd_data = apple1_kbd_conv[loop];
					}
					loop = 51;
				}
			}
		}
	
		return (0);
	} };
	
	/* || */
	
	public static ReadHandlerPtr apple1_pia0_kbdin  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (apple1_kbd_data | 0x80);
	} };
	
	public static ReadHandlerPtr apple1_pia0_dsprdy  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return (0x00);					   /* Screen always ready */
	} };
	
	public static ReadHandlerPtr apple1_pia0_kbdrdy  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (apple1_kbd_data != 0) return (1);	/* Key available */
	
		return (0x00);
	} };
	
	public static WriteHandlerPtr apple1_pia0_dspout = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		apple1_vh_dsp_w(data);
	} };
	
}
