package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для класса {@link Burger}.
 * Используются моки для Bun и Ingredient, параметризация для проверки расчёта цены.
 */
@RunWith(MockitoJUnitRunner.class)
public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun bunMock;

    @Mock
    private Ingredient ingredientMock1;
    @Mock
    private Ingredient ingredientMock2;
    @Mock
    private Ingredient ingredientMock3;

    @Before
    public void setUp() {
        burger = new Burger();
    }

    // ================== setBuns ==================
    @Test
    public void setBunsShouldStoreBun() {
        burger.setBuns(bunMock);
        assertEquals("Bun should be set correctly", bunMock, burger.bun);
    }

    // ================== addIngredient ==================
    @Test
    public void addIngredientShouldAddToEmptyList() {
        burger.addIngredient(ingredientMock1);
        assertEquals("List size should be 1", 1, burger.ingredients.size());
        assertTrue("Ingredient should be in list", burger.ingredients.contains(ingredientMock1));
    }

    @Test
    public void addIngredientShouldAddMultipleIngredients() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        assertEquals("List size should be 2", 2, burger.ingredients.size());
        assertSame("First ingredient should be ingredientMock1", ingredientMock1, burger.ingredients.get(0));
        assertSame("Second ingredient should be ingredientMock2", ingredientMock2, burger.ingredients.get(1));
    }

    // ================== removeIngredient ==================
    @Test
    public void removeIngredientShouldRemoveByIndex() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.removeIngredient(0);
        assertEquals("List size should be 1", 1, burger.ingredients.size());
        assertSame("Remaining ingredient should be ingredientMock2", ingredientMock2, burger.ingredients.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeIngredientWithInvalidIndexShouldThrowException() {
        // Список пуст, удаление элемента 0 вызовет исключение
        burger.removeIngredient(0);
    }

    // ================== moveIngredient ==================
    @Test
    public void moveIngredientShouldMoveFromStartToEnd() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.addIngredient(ingredientMock3);
        burger.moveIngredient(0, 2);
        assertEquals("First ingredient should become ingredientMock2", ingredientMock2, burger.ingredients.get(0));
        assertEquals("Second ingredient should become ingredientMock3", ingredientMock3, burger.ingredients.get(1));
        assertEquals("Third ingredient should become ingredientMock1", ingredientMock1, burger.ingredients.get(2));
    }

    @Test
    public void moveIngredientShouldMoveFromEndToStart() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.addIngredient(ingredientMock3);
        burger.moveIngredient(2, 0);
        assertEquals("First ingredient should become ingredientMock3", ingredientMock3, burger.ingredients.get(0));
        assertEquals("Second ingredient should become ingredientMock1", ingredientMock1, burger.ingredients.get(1));
        assertEquals("Third ingredient should become ingredientMock2", ingredientMock2, burger.ingredients.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveIngredientWithInvalidIndexShouldThrowException() {
        burger.addIngredient(ingredientMock1);
        burger.moveIngredient(5, 0); // индекс 5 вне границ
    }

    // ================== getPrice ==================
    @Test
    public void getPriceShouldReturnCorrectSumWithNoIngredients() {
        when(bunMock.getPrice()).thenReturn(150.0f);
        burger.setBuns(bunMock);
        float expected = 300.0f; // 2 * 150
        assertEquals("Price should be 2 * bun price", expected, burger.getPrice(), 0.001);
    }

    @Test
    public void getPriceShouldReturnCorrectSumWithIngredients() {
        when(bunMock.getPrice()).thenReturn(200.0f);
        burger.setBuns(bunMock);

        when(ingredientMock1.getPrice()).thenReturn(50.0f);
        when(ingredientMock2.getPrice()).thenReturn(70.0f);
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);

        float expected = 520.0f; // 2*200 + 50 + 70
        assertEquals("Price should include bun and ingredients", expected, burger.getPrice(), 0.001);
    }

    // Параметризованный тест для getPrice()
    @RunWith(Parameterized.class)
    public static class BurgerPriceParameterizedTest {

        @Parameterized.Parameter(0)
        public float bunPrice;

        @Parameterized.Parameter(1)
        public float[] ingredientPrices;

        @Parameterized.Parameter(2)
        public float expectedTotal;

        @Parameterized.Parameters(name = "Bun: {0}, ingredients: {1} => total: {2}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {100.0f, new float[]{50.0f, 70.0f}, 320.0f},  // 2*100 + 50 + 70 = 320
                    {200.0f, new float[]{100.0f}, 500.0f},        // 2*200 + 100 = 500
                    {150.0f, new float[]{}, 300.0f},              // 2*150 = 300
                    {175.5f, new float[]{25.3f, 30.2f, 10.5f}, 417.5f} // 2*175.5 + 66 = 417
            });
        }

        @Test
        public void getPriceShouldReturnCorrectSumForMultipleScenarios() {
            Burger burger = new Burger();
            Bun bun = mock(Bun.class);
            when(bun.getPrice()).thenReturn(bunPrice);
            burger.setBuns(bun);

            for (float price : ingredientPrices) {
                Ingredient ing = mock(Ingredient.class);
                when(ing.getPrice()).thenReturn(price);
                burger.addIngredient(ing);
            }

            assertEquals(expectedTotal, burger.getPrice(), 0.001);
        }
    }

    // ================== getReceipt ==================
    @Test
    public void getReceiptShouldReturnFormattedStringWithIngredients() {
        // Настройка моков
        when(bunMock.getName()).thenReturn("black bun");
        when(bunMock.getPrice()).thenReturn(100.0f);
        burger.setBuns(bunMock);

        when(ingredientMock1.getType()).thenReturn(IngredientType.SAUCE);
        when(ingredientMock1.getName()).thenReturn("hot sauce");
        when(ingredientMock1.getPrice()).thenReturn(50.0f);

        when(ingredientMock2.getType()).thenReturn(IngredientType.FILLING);
        when(ingredientMock2.getName()).thenReturn("cutlet");
        when(ingredientMock2.getPrice()).thenReturn(70.0f);

        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);

        String receipt = burger.getReceipt();

        // Ожидаемая строка с учётом форматирования цены через %f (6 знаков после запятой)
        String expected = String.format("(==== black bun ====)%n" +
                "= sauce hot sauce =%n" +
                "= filling cutlet =%n" +
                "(==== black bun ====)%n" +
                "%nPrice: %f%n", 320.0f);
        assertEquals(expected, receipt);
    }

    @Test
    public void getReceiptShouldHandleNoIngredients() {
        when(bunMock.getName()).thenReturn("red bun");
        when(bunMock.getPrice()).thenReturn(300.0f);
        burger.setBuns(bunMock);

        String receipt = burger.getReceipt();

        String expected = String.format("(==== red bun ====)%n" +
                "(==== red bun ====)%n" +
                "%nPrice: %f%n", 600.0f);
        assertEquals(expected, receipt);
    }
}