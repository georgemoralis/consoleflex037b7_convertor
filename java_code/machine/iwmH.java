#ifndef IWM_H
#define IWM_H

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class iwmH
{
	
	enum {
		IWM_FLOPPY_ALLOW400K	= 1,
		IWM_FLOPPY_ALLOW800K	= 2
	};
	
	int iwm_floppy_init(int id, int allowablesizes);
	void iwm_floppy_exit(int id);
	
	int iwm_r(int offset);
	void iwm_w(int offset, int data);
	void iwm_set_sel_line(int sel);
	
	#endif /* IWM_H */
}
