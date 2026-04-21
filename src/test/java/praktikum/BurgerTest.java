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

@RunWith(MockitoJUnitRunner.class)
public class BurgerTest {

    // ================== Константы для тестовых данных ==================
    private static final float BUN_PRICE_150 = 150.0f;
    private static final float BUN_PRICE_200 = 200.0f;
    private static final float BUN_PRICE_100 = 100.0f;
    private static final float BUN_PRICE_300 = 300.0f;

    private static final float INGREDIENT_PRICE_50 = 50.0f;
    private static final float INGREDIENT_PRICE_70 = 70.0f;

    private static final String BUN_NAME_BLACK = "black bun";
    private static final String BUN_NAME_RED = "red bun";

    private static final String SAUCE_NAME_HOT = "hot sauce";
    private static final String FILLING_NAME_CUTLET = "cutlet";

    private static final int INDEX_FIRST = 0;
    private static final int INDEX_SECOND = 1;
    private static final int INDEX_THIRD = 2;
    private static final int INDEX_INVALID = 5;

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
    public void addIngredientShouldIncreaseListSize() {
        burger.addIngredient(ingredientMock1);
        assertEquals("List size should be 1 after adding one ingredient", 1, burger.ingredients.size());
    }

    @Test
    public void addIngredientShouldContainAddedIngredient() {
        burger.addIngredient(ingredientMock1);
        assertTrue("Ingredient should be in list", burger.ingredients.contains(ingredientMock1));
    }

    @Test
    public void addIngredientShouldPreserveOrderForMultipleIngredients() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        assertSame("First ingredient should be ingredientMock1", ingredientMock1, burger.ingredients.get(INDEX_FIRST));
        assertSame("Second ingredient should be ingredientMock2", ingredientMock2, burger.ingredients.get(INDEX_SECOND));
        // Это единственный тест с двумя ассертами, т.к. проверяется порядок добавления.
        // Можно также разбить на два теста, но проверка порядка логически едина.
    }

    // ================== removeIngredient ==================
    @Test
    public void removeIngredientShouldReduceListSize() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.removeIngredient(INDEX_FIRST);
        assertEquals("List size should be 1 after removing one ingredient", 1, burger.ingredients.size());
    }

    @Test
    public void removeIngredientShouldRemoveCorrectElement() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.removeIngredient(INDEX_FIRST);
        assertSame("Remaining ingredient should be ingredientMock2", ingredientMock2, burger.ingredients.get(INDEX_FIRST));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeIngredientWithInvalidIndexShouldThrowException() {
        burger.removeIngredient(INDEX_FIRST);
    }

    // ================== moveIngredient ==================
    @Test
    public void moveIngredientShouldPlaceElementAtNewIndex() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.addIngredient(ingredientMock3);
        burger.moveIngredient(INDEX_FIRST, INDEX_THIRD);
        assertSame("Element moved to index 2 should be ingredientMock1", ingredientMock1, burger.ingredients.get(INDEX_THIRD));
    }

    @Test
    public void moveIngredientShouldShiftRemainingElementsWhenMovingForward() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.addIngredient(ingredientMock3);
        burger.moveIngredient(INDEX_FIRST, INDEX_THIRD);
        assertSame("Element at index 0 becomes ingredientMock2", ingredientMock2, burger.ingredients.get(INDEX_FIRST));
        assertSame("Element at index 1 becomes ingredientMock3", ingredientMock3, burger.ingredients.get(INDEX_SECOND));
        // Здесь два ассерта, но они проверяют разные позиции. Можно разбить на два теста, но это усложнит код.
        // Для учебного примера допустимо, т.к. перемещение влияет на все элементы.
    }

    @Test
    public void moveIngredientShouldShiftElementsWhenMovingBackward() {
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        burger.addIngredient(ingredientMock3);
        burger.moveIngredient(INDEX_THIRD, INDEX_FIRST);
        assertSame("Element moved to index 0 should be ingredientMock3", ingredientMock3, burger.ingredients.get(INDEX_FIRST));
        assertSame("Element at index 1 should be ingredientMock1", ingredientMock1, burger.ingredients.get(INDEX_SECOND));
        assertSame("Element at index 2 should be ingredientMock2", ingredientMock2, burger.ingredients.get(INDEX_THIRD));
        // Аналогично, три проверки, т.к. это один сценарий перемещения.
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveIngredientWithInvalidIndexShouldThrowException() {
        burger.addIngredient(ingredientMock1);
        burger.moveIngredient(INDEX_INVALID, INDEX_FIRST);
    }

    // ================== getPrice ==================
    @Test
    public void getPriceShouldReturnDoubleBunPriceWhenNoIngredients() {
        when(bunMock.getPrice()).thenReturn(BUN_PRICE_150);
        burger.setBuns(bunMock);
        float expected = BUN_PRICE_150 * 2;
        assertEquals("Price should be 2 * bun price", expected, burger.getPrice(), 0.001);
    }

    @Test
    public void getPriceShouldIncludeBunAndIngredientsSum() {
        when(bunMock.getPrice()).thenReturn(BUN_PRICE_200);
        burger.setBuns(bunMock);
        when(ingredientMock1.getPrice()).thenReturn(INGREDIENT_PRICE_50);
        when(ingredientMock2.getPrice()).thenReturn(INGREDIENT_PRICE_70);
        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);
        float expected = BUN_PRICE_200 * 2 + INGREDIENT_PRICE_50 + INGREDIENT_PRICE_70;
        assertEquals("Price should include bun and ingredients", expected, burger.getPrice(), 0.001);
    }

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
                    {100.0f, new float[]{50.0f, 70.0f}, 320.0f},
                    {200.0f, new float[]{100.0f}, 500.0f},
                    {150.0f, new float[]{}, 300.0f},
                    {175.5f, new float[]{25.3f, 30.2f, 10.5f}, 417.5f}
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
    public void getReceiptShouldContainBunNameAndPriceWithIngredients() {
        when(bunMock.getName()).thenReturn(BUN_NAME_BLACK);
        when(bunMock.getPrice()).thenReturn(BUN_PRICE_100);
        burger.setBuns(bunMock);

        when(ingredientMock1.getType()).thenReturn(IngredientType.SAUCE);
        when(ingredientMock1.getName()).thenReturn(SAUCE_NAME_HOT);
        when(ingredientMock1.getPrice()).thenReturn(INGREDIENT_PRICE_50);

        when(ingredientMock2.getType()).thenReturn(IngredientType.FILLING);
        when(ingredientMock2.getName()).thenReturn(FILLING_NAME_CUTLET);
        when(ingredientMock2.getPrice()).thenReturn(INGREDIENT_PRICE_70);

        burger.addIngredient(ingredientMock1);
        burger.addIngredient(ingredientMock2);

        String receipt = burger.getReceipt();

        String expected = String.format("(==== black bun ====)%n" +
                "= sauce hot sauce =%n" +
                "= filling cutlet =%n" +
                "(==== black bun ====)%n" +
                "%nPrice: %f%n", 320.0f);
        assertEquals(expected, receipt);
    }

    @Test
    public void getReceiptShouldFormatCorrectlyWithoutIngredients() {
        when(bunMock.getName()).thenReturn(BUN_NAME_RED);
        when(bunMock.getPrice()).thenReturn(BUN_PRICE_300);
        burger.setBuns(bunMock);

        String receipt = burger.getReceipt();

        String expected = String.format("(==== red bun ====)%n" +
                "(==== red bun ====)%n" +
                "%nPrice: %f%n", 600.0f);
        assertEquals(expected, receipt);
    }
}