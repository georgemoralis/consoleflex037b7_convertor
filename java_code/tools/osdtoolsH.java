/* osdtools.h
 *
 * OS dependant code for the tools
 */

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package tools;

public class osdtoolsH
{
	
	#ifdef WIN32
	
	#define osd_mkdir(dir)	mkdir(dir)
	#define PATH_SEPARATOR	"\\"
	#define strncasecmp	strnicmp
	
	inline const char *basename(const char *name)
	{
		const char *s = name;
		const char *result = name;
	
		for (s = name; *s; s++) {
			if ((*s == '\\') || (*s == '/'))
				result = s + 1;
		}
		return result;
	}
	
	#else
	
	#define osd_mkdir(dir)	mkdir(dir, 0)
	#define PATH_SEPARATOR	"/"
	
	#endif
	
	}
