/***************************************************************************

  machine.c

  Functions to emulate general aspects of the machine (RAM, ROM, interrupts,
  I/O ports)

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class enterp
{
	
	extern UBytePtr Enterprise_RAM;
	
	
	int ep128_flop_specified[4] = {0,0,0,0};
	
	public static InitMachinePtr enterprise_init_machine = new InitMachinePtr() { public void handler() 
	{
		/* allocate memory. */
		/* I am allocating it because I control how the ram is
		 * accessed. Mame will not allocate any memory because all
		 * the memory regions have been defined as MRA_BANK?
		*/
		/* 128k memory, 32k for dummy read/write operations
		 * where memory bank is not defined
		 */
		Enterprise_RAM = malloc((128*1024)+32768);
		if (!Enterprise_RAM) return;
	
		/* initialise the hardware */
		Enterprise_Initialise();
	} };
	
	void enterprise_shutdown_machine(void)
	{
		if (Enterprise_RAM != NULL)
			free(Enterprise_RAM);
	
		Enterprise_RAM = NULL;
	}
	
	int enterprise_floppy_init(int id)
	{
		ep128_flop_specified[id] = device_filename(IO_FLOPPY,id) != NULL;
	
	    return 0;
	}
}
