#ifndef __VIC6567_H_
#define __VIC6567_H_


/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class vic6567H
{
	
	/*
	 * if you need this chip in another mame/mess emulation than let it me know
	 * I will split this from the c64 driver
	 * peter.trauner@jk.uni-linz.ac.at
	 * 1. 1. 2000
	 * look at mess/systems/c64.c and mess/machine/c64.c
	 * on how to use it
	 */
	
	/* call to init videodriver */
	/* dma_read: videochip fetched 1 byte data from system bus */
	extern void vic6567_init (int vic2e, int pal, int (*dma_read) (int),
							  int (*dma_read_color) (int), void (*irq) (int));
	
	extern void vic4567_init (int pal, int (*dma_read) (int),
							  int (*dma_read_color) (int), void (*irq) (int),
							  void (*param_port_changed)(int));
	
	extern void vic2_set_rastering(int onoff);
	
	#define VIC6567_VRETRACERATE 60
	#define VIC6569_VRETRACERATE 50
	#define VIC2_VRETRACERATE (vic2.pal?VIC6569_VRETRACERATE:VIC6567_VRETRACERATE)
	#define VIC2_HRETRACERATE 15625
	
	/* to be inserted in MachineDriver-Structure */
	/* the following values depend on the VIC clock,
	 * but to achieve TV-frequency the clock must have a fix frequency */
	#define VIC2_HSIZE	320
	#define VIC2_VSIZE	200
	/* of course you clock select an other clock, but for accurate */
	/* video timing */
	#define VIC6567_CLOCK	(8180000/8)
	#define VIC6569_CLOCK	(7880000/8)
	/* pixel clock 8 mhz */
	/* accesses to memory with 2 megahertz */
	/* needs 2 memory accesses for 8 pixel */
	/* + sprite + */
	/* but system clock 1 megahertz */
	/* cpu driven with one (visible screen area) */
	#define VIC2_CLOCK ((vic2.pal?VIC6569_CLOCK:VIC6567_CLOCK))
	
	/* pal 50 Hz vertical screen refresh, screen consists of 312 lines
	 * ntsc 60 Hz vertical screen refresh, screen consists of 262 lines */
	#define VIC6567_LINES 261
	#define VIC6569_LINES 312
	#define VIC2_LINES (vic2.pal?VIC6569_LINES:VIC6567_LINES)
	
	extern extern extern extern 
	extern unsigned char vic2_palette[16 * 3];
	extern unsigned char vic3_palette[0x100 * 3];
	
	/* to be inserted in GameDriver-Structure */
	
	/* to be called when writting to port */
	extern WRITE_HANDLER ( vic2_port_w );
	
	/* to be called when reading from port */
	extern READ_HANDLER ( vic2_port_r );
	
	extern void vic2_lightpen_write (int level);
	
	
	
	/* to be called each vertical retrace */
	extern 
	extern void (*vic2_display_state)(PRASTER *This); /* calls machine after rastering frame*/
	
	/* private area */
	
	/*extern UINT8 vic2[]; */
	/*extern bool vic2_pal; */
	
	#endif
	
}
