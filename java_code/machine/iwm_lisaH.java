#ifndef IWM_H
#define IWM_H

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class iwm_lisaH
{
	
	enum {
		IWM_FLOPPY_ALLOW400K	= 1,
		IWM_FLOPPY_ALLOW800K	= 2
	};
	
	int iwm_lisa_floppy_init(int id, int allowablesizes);
	void iwm_lisa_floppy_exit(int id);
	
	int iwm_lisa_r(int offset);
	void iwm_lisa_w(int offset, int data);
	void iwm_lisa_set_head_line(int head);
	/*
	#endif /* IWM_H */
}
