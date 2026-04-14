import enums.ActionLetter;
import model.*;
import util.UniversalArray;
import util.UniversalArrayImpl;

import java.util.Scanner;

public class AppRunner {

    private final UniversalArray<Product> products = new UniversalArrayImpl<>();

    private final CoinAcceptor coinAcceptor;
    private final BillAcceptor billAcceptor;

    private static boolean isExit = false;

    private AppRunner() {
        products.addAll(new Product[]{
                new Water(ActionLetter.B, 20),
                new CocaCola(ActionLetter.C, 50),
                new Soda(ActionLetter.D, 30),
                new Snickers(ActionLetter.E, 80),
                new Mars(ActionLetter.F, 80),
                new Pistachios(ActionLetter.G, 130)
        });
        coinAcceptor = new CoinAcceptor(100);
        billAcceptor = new BillAcceptor(160);
    }

    public static void run() {
        AppRunner app = new AppRunner();
        while (!isExit) {
            app.startSimulation();
        }
    }

    private void startSimulation() {
        print("В автомате доступны:");
        showProducts(products);

        print("Монет на сумму: " + coinAcceptor.getAmount());
        print("Купюр на сумму: " + billAcceptor.getAmount());

        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        allowProducts.addAll(getAllowedProducts().toArray());
        chooseAction(allowProducts);

    }

    private UniversalArray<Product> getAllowedProducts() {
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        for (int i = 0; i < products.size(); i++) {
            if (coinAcceptor.getAmount() >= products.get(i).getPrice() || billAcceptor.getAmount() >= products.get(i).getPrice()) {
                allowProducts.add(products.get(i));
            }
        }
        return allowProducts;
    }

    private void chooseAction(UniversalArray<Product> products) {
        boolean bool;
        showActions(products);
        print(" h - Выйти");
        print(" a - Пополнить баланс монет");
        print(" l - Пополнить баланс купюр");
        try {
            String action = fromConsole().substring(0, 1);
            if ("h".equalsIgnoreCase(action)) {
                isExit = true;
            } else if ("a".equalsIgnoreCase(action) || "l".equalsIgnoreCase(action)) {
                topUpYourBalance(action);
            } else {
                for (int i = 0; i < products.size(); i++) {
                    if (products.get(i).getActionLetter().equals(ActionLetter.valueOf(action.toUpperCase()))) {
                        chooseAPaymentMethod(i);
                    }
                }
            }
        } catch (IllegalArgumentException e){
            print("Недопустимая буква. Попробуйте еще раз.");
            chooseAction(products);
        } catch (StringIndexOutOfBoundsException sioobe){
            print("Недопустимая буква. Попробуйте еще раз.");
            chooseAction(products);
        }

    }

    private void topUpYourBalance (String action) {
        boolean bool;
        if (action.equalsIgnoreCase("a")){
            do {
                print("Введите сумму пополнения:");
                String str = fromConsole();
                int upAmount;
                try {
                    upAmount = Integer.parseInt(str);
                    if (upAmount > 0){
                        coinAcceptor.setAmount(coinAcceptor.getAmount() + upAmount);
                        bool = false;
                    } else {
                        System.out.println("Вы не можете вести эту сумму! Попробуйте еще!");
                        bool = true;
                    }
                } catch (NumberFormatException e) {
                    print("Попробуйте еще раз!");
                    bool = true;
                    break;
                }
            } while (bool);
        } else if (action.equalsIgnoreCase("l")) {
            do {
                print("Введите сумму пополнения:");
                String str = fromConsole();
                int upAmount;
                try {
                    upAmount = Integer.parseInt(str);
                    if (upAmount > 0){
                        billAcceptor.setAmount(billAcceptor.getAmount() + upAmount);
                        bool = false;
                    } else {
                        System.out.println("Вы не можете вести эту сумму! Попробуйте еще!");
                        bool = true;
                    }
                } catch (NumberFormatException e) {
                    print("Попробуйте еще раз!");
                    bool = true;
                    break;
                }
            } while (bool);
        }
    }

    private void chooseAPaymentMethod (int i){
        boolean bool;
        do {
            print("Выберите способ оплаты: нажмите 1, если платите монетой, и 2, если купюрой.");
            String choosePaymentMethod = fromConsole().substring(0, 1);
            try {
                if (choosePaymentMethod.equalsIgnoreCase("1")){
                    if (coinAcceptor.getAmount() >= products.get(i).getPrice()){
                        coinAcceptor.setAmount(coinAcceptor.getAmount() - products.get(i).getPrice());
                        print("Вы купили монетой " + products.get(i).getName());
                        bool = false;
                        break;
                    } else {
                        print("У вас недостаточно монет, чтобы купить этот продукт! " +
                                "Попробуйте заплатить купюрой, или пополните баланс!");
                        chooseAction(products);
                        bool = true;
                        break;
                    }
                } else if (choosePaymentMethod.equalsIgnoreCase("2")){
                    if (billAcceptor.getAmount() >= products.get(i).getPrice()){
                        billAcceptor.setAmount(billAcceptor.getAmount() - products.get(i).getPrice());
                        print("Вы купили купюрой " + products.get(i).getName());
                        bool = false;
                        break;
                    } else {
                        print("У вас недостаточно купюр, чтобы купить этот продукт! " +
                                "Попробуйте заплатить монетой, или пополните баланс!");
                        chooseAction(products);
                        bool = true;
                        break;
                    }
                } else {
                    print("Ошибка! Попробуйте снова!");
                    bool = true;
                    break;
                }
            } catch (IllegalArgumentException e) {
                print("Недопустимая буква. Попробуйте еще раз.");
                chooseAction(products);
                bool = true;
            }
        } while (bool);
    }

    private void showActions(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(String.format(" %s - %s", products.get(i).getActionLetter().getValue(), products.get(i).getName()));
        }
    }

    private String fromConsole() {
        return new Scanner(System.in).nextLine();
    }

    private void showProducts(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(products.get(i).toString());
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }



}
