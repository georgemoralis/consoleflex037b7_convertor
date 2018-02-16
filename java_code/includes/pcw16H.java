#define PCW16_BORDER_HEIGHT 8
#define PCW16_BORDER_WIDTH 8
#define PCW16_NUM_COLOURS 32
#define PCW16_DISPLAY_WIDTH 640
#define PCW16_DISPLAY_HEIGHT 480

#define PCW16_SCREEN_WIDTH	(PCW16_DISPLAY_WIDTH + (PCW16_BORDER_WIDTH<<1)) 
#define PCW16_SCREEN_HEIGHT	(PCW16_DISPLAY_HEIGHT  + (PCW16_BORDER_HEIGHT<<1))

void pcw16_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable, const UBytePtr color_prom);
