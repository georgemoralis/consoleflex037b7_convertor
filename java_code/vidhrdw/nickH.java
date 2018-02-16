#ifndef __NICK_GRAPHICS_CHIP_MESS_INCLUDED__
#define __NICK_GRAPHICS_CHIP_MESS_INCLUDED__

/**************************************
NICK GRAPHICS CHIP
***************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class nickH
{
	
	extern int	Nick_vh_start(void);
	extern void 	Nick_DoScreen(struct osd_bitmap *bm);
	extern void	Nick_vh_stop(void);
	
	extern int Nick_reg_r(int);
	extern void Nick_reg_w(int, int);
	
	#endif
}
