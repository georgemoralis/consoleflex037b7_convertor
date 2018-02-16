/* TOSHIBA TC8521 REAL TIME CLOCK */

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class tc8521H
{
	
	READ_HANDLER(tc8521_r);
	WRITE_HANDLER(tc8521_w);
	
	struct tc8521_interface
	{
	        /* tc8521 causes an interrupt */
	      void (*interrupt_1hz_callback)(int);
	      void (*interrupt_16hz_callback)(int);
	};
	
	void tc8521_init(struct tc8521_interface *);
	
}
