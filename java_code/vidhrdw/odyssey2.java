/***************************************************************************

  vidhrdw/odyssey2.c

  Routines to control the Adventurevision video hardware

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class odyssey2
{
	
	static UINT8 *odyssey2_display;
	int odyssey2_vh_hpos;
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	public static VhStartPtr odyssey2_vh_start = new VhStartPtr() { public int handler() 
	{
	    odyssey2_vh_hpos = 0;
		odyssey2_display = (UINT8 *)malloc(8 * 8 * 256);
		if( !odyssey2_display )
			return 1;
		memset(odyssey2_display, 0, 8 * 8 * 256);
	    return 0;
	} };
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	
	void odyssey2_vh_init_palette(UBytePtr game_palette, unsigned short *game_colortable,const UBytePtr color_prom)
	{
		int i;
		for( i = 0; i < 8; i++ )
		{
			game_palette[i*3+0] = i * 0x22; /* 8 shades of RED */
			game_palette[i*3+1] = 0x00;
			game_palette[i*3+2] = 0x00;
			game_colortable[i*2+0] = 0;
			game_colortable[i*2+0] = i;
		}
		game_palette[8*3+0] = 0x55; /* DK GREY - for MAME text only */
		game_palette[8*3+1] = 0x55;
		game_palette[8*3+2] = 0x55;
		game_palette[9*3+0] = 0xf0; /* LT GREY - for MAME text only */
		game_palette[9*3+1] = 0xf0;
		game_palette[9*3+2] = 0xf0;
	}
	
	public static VhStopPtr odyssey2_vh_stop = new VhStopPtr() { public void handler() 
	{
		if (odyssey2_display != 0)
			free(odyssey2_display);
		odyssey2_display = NULL;
	} };
	
	void odyssey2_vh_write(int data)
	{
		return;
	}
	
	void odyssey2_vh_update(int x)
	{
	    return;
	}
	
	
	/***************************************************************************
	
	  Refresh the video screen
	
	***************************************************************************/
	
	public static VhUpdatePtr odyssey2_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		return;
	} };
	
	
}
