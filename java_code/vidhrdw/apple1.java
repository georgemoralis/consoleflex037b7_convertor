/***************************************************************************

  apple1.c

  Functions to emulate the video hardware of the Jupiter Ace.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class apple1
{
	
	static	int	dsp_pntr;
	
	public static VhStartPtr apple1_vh_start = new VhStartPtr() { public int handler() 
	{
		dsp_pntr = 0;
		if (!(videoram = malloc (videoram_size[0] = 40 * 24)))
			return (1);;
		if (generic_vh_start ())
			return (1);
	
		memset (videoram, 0, videoram_size[0]);
		memset (dirtybuffer, 1, videoram_size[0]);
		return (0);
	} };
	
	public static VhStopPtr apple1_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop ();
	} };
	
	void	apple1_vh_dsp_w (int data)
	{
		int	loop;
	
		data &= 0x7f;
	
		switch (data) {
		case 0x0a:
		case 0x0d:
			dirtybuffer[dsp_pntr] = 1;
			dsp_pntr += 40 - (dsp_pntr % 40);
			break;
		case 0x5f:
			dirtybuffer[dsp_pntr] = 1;
			if (dsp_pntr != 0) dsp_pntr--;
			videoram.write(dsp_pntr,0);
			break;
		default:
			dirtybuffer[dsp_pntr] = 1;
			videoram.write(dsp_pntr,data);
			dsp_pntr++;
			break;
		}
	
	/* || */
	
		if (dsp_pntr >= 960)
		{
			for (loop = 40; loop < 960; loop++)
			{
				if (!videoram.read(loop - 40)|| ((videoram.read(loop - 40)> 1) &&
											(videoram.read(loop - 40)<= 32)) ||
											(videoram.read(loop - 40)> 96))
				{
					if (!(!videoram.read(loop)|| ((videoram.read(loop)> 1) &&
											(videoram.read(loop)<= 32)) ||
											(videoram.read(loop)> 96)))
					{
						videoram.write(loop - 40,videoram[loop]);
						dirtybuffer[loop - 40] = 1;
					}
				}
				else if (videoram.read(loop - 40)!= videoram.read(loop))
				{
					videoram.write(loop - 40,videoram[loop]);
					dirtybuffer[loop - 40] = 1;
				}
			}
			for (loop = 920; loop < 960; loop++)
			{
				if (!(!videoram.read(loop)|| ((videoram.read(loop)> 1) &&
							(videoram.read(loop)<= 32)) || (videoram.read(loop)> 96)))
				{
					videoram.write(loop,0);
					dirtybuffer[loop] = 1;
				}
			}
			dsp_pntr -= 40;
		}
	}
	
	void	apple1_vh_dsp_clr (void)
	{
		int	loop;
	
		dirtybuffer[dsp_pntr] = 1;
		dsp_pntr = 0;
		for (loop = 0; loop < 960; loop++)
		{
			if (!(!videoram.read(loop)|| ((videoram.read(loop)> 1) &&
							(videoram.read(loop)<= 32)) || (videoram.read(loop)> 96)))
			{
				dirtybuffer[loop] = 1;
			}
			videoram.write(loop,0);
		}
	}
	
	public static VhUpdatePtr apple1_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int offs;
		int code;
		int sx, sy;
	
		/* do we need a full refresh? */
	
		if (full_refresh != 0) memset (dirtybuffer, 1, videoram_size[0]);
	
		for (offs = 0; offs < videoram_size[0]; offs++ )
		{
			if (dirtybuffer[offs] || (offs == dsp_pntr))
			{
				if (offs == dsp_pntr) code = 1;
				else code = videoram.read(offs);
				sy = (offs / 40) * 8;
				sx = (offs % 40) * 6;
	
				drawgfx (bitmap, Machine.gfx[0], code, 1,
				  0, 0, sx,sy, &Machine.visible_area, TRANSPARENCY_NONE, 0);
	
				dirtybuffer[offs] = 0;
			}
		}
	} };
}
