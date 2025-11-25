
package boundType;
public sealed interface Payment<T> permits BankTransfer, CreditCard, PayPal, Foo {
    void processPayment(double amount);
}

non-sealed class  Foo implements Payment{

    @Override
    public void processPayment(double amount) {

    }
}