extern extern void set_last_border_color (int NewColor);
extern void draw_border(struct osd_bitmap *bitmap, int full_refresh,
                int TopBorderLines, int ScreenLines, int BottomBorderLines,
                int LeftBorderPixels, int ScreenPixels, int RightBorderPixels,
                int LeftBorderCycles, int ScreenCycles, int RightBorderCycles,
                int HorizontalRetraceCycles, int VRetraceTime, int EventID);
