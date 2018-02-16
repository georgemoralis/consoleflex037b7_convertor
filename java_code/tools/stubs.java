/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package tools;

public class stubs
{
	
	/* Variables to hold the status of various game options */
	static FILE *errorlog;
	
	
	void CLIB_DECL logerror(const char *text,...)
	{
		va_list arg;
		va_start(arg,text);
		if (errorlog != 0)
			vfprintf(errorlog,text,arg);
		va_end(arg);
	}
	
	
}
