#ifndef __C1551_H_
#define __C1551_H_

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class c1551H
{
	
	/* must be called before other functions */
	
	#define IODEVICE_CBM_DRIVE \
	{\
	   IO_FLOPPY,          /* type */\
	   2,				   /* count */\
	   "d64\0",            /* G64 later *//*file extensions */\
	   IO_RESET_NONE,	   /* reset if file changed */\
	   NULL,               /* id */\
	   cbm_drive_attach_image,        /* init */\
	   NULL,			   /* exit */\
	   NULL,               /* info */\
	   NULL,               /* open */\
	   NULL,               /* close */\
	   NULL,               /* status */\
	   NULL,               /* seek */\
	   NULL,			   /* tell */\
	   NULL,               /* input */\
	   NULL,               /* output */\
	   NULL,               /* input_chunk */\
	   NULL                /* output_chunk */\
	}
	
	#define IEC 1
	#define SERIAL 2
	#define IEEE 3
	void cbm_drive_0_config (int interface, int serialnr);
	void cbm_drive_1_config (int interface, int serialnr);
	
	/* open an d64 image */
	int cbm_drive_attach_image (int id);
	
	/* load *.prg files directy from filesystem (rom directory) */
	int cbm_drive_attach_fs (int id);
	
	/* delivers status for displaying */
	extern void cbm_drive_0_status (char *text, int size);
	extern void cbm_drive_1_status (char *text, int size);
	
	/* iec interface c16/c1551 */
	void c1551_0_write_data (int data);
	void c1551_0_write_handshake (int data);
	
	void c1551_1_write_data (int data);
	void c1551_1_write_handshake (int data);
	
	/* serial bus vc20/c64/c16/vc1541 and some printer */
	void cbm_serial_reset_write (int level);
	void cbm_serial_atn_write (int level);
	void cbm_serial_data_write (int level);
	void cbm_serial_clock_write (int level);
	void cbm_serial_request_write (int level);
	
	/* private */
	extern CBM_Drive cbm_drive[2];
	
	#endif
}
