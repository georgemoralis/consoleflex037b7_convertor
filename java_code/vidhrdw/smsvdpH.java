
#ifndef _SMSVDP_H_
#define _SMSVDP_H_

#define STATUS_VINT     (0x80)  /* Pending vertical interrupt flag */
#define STATUS_HINT     (0x40)  /* Pending horizontal interrupt flag */
#define STATUS_SPRCOL   (0x20)  /* Object collision flag */

/* Function prototypes */

READ_HANDLER ( sms_vdp_curline_r );
int SMSVDP_start (int vdp_type);
READ_HANDLER ( sms_vdp_data_r );
WRITE_HANDLER ( sms_vdp_data_w );
READ_HANDLER ( sms_vdp_ctrl_r );
WRITE_HANDLER ( sms_vdp_ctrl_w );
void sms_refresh_line(struct osd_bitmap *bitmap, int line);

#endif /* _SMSVDP_H_ */

