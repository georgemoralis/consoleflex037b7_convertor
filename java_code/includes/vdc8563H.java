#ifndef __VDC8563_H_
#define __VDC8563_H_
/***************************************************************************

  CBM Video Device Chip 8563

  copyright 2000 peter.trauner@jk.uni-linz.ac.at

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class vdc8563H
{
	
	#ifdef __cplusplus
	extern "C" {
	#endif
	
	/* call to init videodriver */
	extern void vdc8563_init (UINT8 *memory, int ram16konly);
	
	extern void vdc8563_set_rastering(int on);
	
	extern #define vdc8563_vh_stop praster_vh_stop
	#define vdc8563_vh_screenrefresh praster_vh_screenrefresh
	#define vdc8563_raster_irq praster_raster_irq
	
	#define vdc8563_update praster_2_update
	
	extern unsigned char vdc8563_palette[16 * 3];
	
	/* to be called when writting to port */
	extern WRITE_HANDLER ( vdc8563_port_w );
	
	/* to be called when reading from port */
	extern READ_HANDLER ( vdc8563_port_r );
	
	extern void vdc8563_status (char *text, int size);
	
	#ifdef __cplusplus
	}
	#endif
	
	#endif
}
