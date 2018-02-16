/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class z88H
{
	#define Z88_NUM_COLOURS 2
	
	#define Z88_SCREEN_WIDTH        640
	#define Z88_SCREEN_HEIGHT       64
	
	void z88_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable, const UBytePtr color_prom);
	
	struct blink_hw
	{
		int pb[4];
		int sbr;
	        int sbf;
	
		int com;
		int ints;
		int sta;
		int ack;
		int tack;
		int tmk;
		int mem[4];
		int tim[5];
		int tsta;
	};
	
}
