/***************************************************************************

  Functions to emulate the video hardware of the Enterprise.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class enterp
{
	
	/***************************************************************************
	  Start the video hardware emulation.
	***************************************************************************/
	int enterprise_vh_start(void) {
	
		Nick_vh_start();
		return 0;
	}
	
	void    enterprise_vh_stop(void) {
		Nick_vh_stop();
	}
	
	/***************************************************************************
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function,
	  it will be called by the main emulation engine.
	***************************************************************************/
	void enterprise_vh_screenrefresh(struct osd_bitmap *bitmap,int fullupdate)
	{
			Nick_DoScreen(bitmap);
	}
	
}
