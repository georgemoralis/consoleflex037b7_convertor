/***************************************************************************

  vidhrdw/coleco.c

  Routines to control the Colecovision video hardware

***************************************************************************/


/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class coleco
{
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	int coleco_vh_start(void)
	{
		return TMS9928A_start(TMS99x8A, 0x4000);
	}
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	void coleco_vh_stop(void)
	{
		TMS9928A_stop();
		return;
	}
	
	
	/***************************************************************************
	
	  Refresh the video screen
	
	***************************************************************************/
	
	/* This routine is called at the start of vblank to refresh the screen */
	void coleco_vh_screenrefresh(struct osd_bitmap *bitmap, int full_refresh)
	{
		TMS9928A_refresh(bitmap, full_refresh);
		return;
	}
}
