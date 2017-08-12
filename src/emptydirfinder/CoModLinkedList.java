package emptydirfinder;

/**
 *
 * @author Тиилл
 */
public class CoModLinkedList<E> {
    private LinkedElement<E> startElement;
    private LinkedElement<E> endElement;
    private int size;

    public CoModLinkedList() {
    }
    
    public void addAtEnd(E element){
        if(size == 0) {
           LinkedElement lelement = new LinkedElement<E>(element);
           this.startElement = lelement;
           this.endElement = lelement;
           size =1 ;
        }else {
            LinkedElement lelement = new LinkedElement<E>(element);
            this.endElement.setNext(lelement);
            this.endElement = lelement;
        }
        
    }
    
    public void addAtStart (E element){
        
    }
    
    private class LinkedElement<E> {
        private E data;
        private LinkedElement next;

        public LinkedElement(E data, LinkedElement next) {
            this.data = data;
            this.next = next;
        }

        public LinkedElement(E data) {
            this.data = data;
        }
        
        public E get(){
            return this.data;
        }
        
        public LinkedElement next(){
            return this.next;
        }

        public void setData(E data) {
            this.data = data;
        }

        public void setNext(LinkedElement next) {
            this.next = next;
        }
        
        
        
        
        
    }
}
