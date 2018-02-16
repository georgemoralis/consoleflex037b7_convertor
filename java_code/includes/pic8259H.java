/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class pic8259H
{
	
	extern WRITE_HANDLER ( pic8259_0_w );
	extern READ_HANDLER ( pic8259_0_r );
	extern WRITE_HANDLER ( pic8259_1_w );
	extern READ_HANDLER ( pic8259_1_r );
	
	extern void pic8259_0_issue_irq(int irq);
	extern void pic8259_1_issue_irq(int irq);
	
	int pic8259_0_irq_pending(int irq);
	int pic8259_1_irq_pending(int irq);
}
