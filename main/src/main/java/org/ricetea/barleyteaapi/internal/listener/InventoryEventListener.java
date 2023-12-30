package org.ricetea.barleyteaapi.internal.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.ApiStatus;
import org.ricetea.barleyteaapi.BarleyTeaAPI;
import org.ricetea.barleyteaapi.api.item.CustomItem;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemAnvil;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemEnchant;
import org.ricetea.barleyteaapi.api.item.feature.FeatureItemGrindstone;
import org.ricetea.barleyteaapi.api.item.feature.data.*;
import org.ricetea.barleyteaapi.api.item.helper.ItemHelper;
import org.ricetea.barleyteaapi.api.item.registration.ItemRegister;
import org.ricetea.barleyteaapi.api.item.render.util.AlternativeItemState;
import org.ricetea.barleyteaapi.internal.linker.ItemFeatureLinker;
import org.ricetea.utils.Lazy;
import org.ricetea.utils.ObjectUtil;
import org.ricetea.utils.WithFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.function.Consumer;

@Singleton
@ApiStatus.Internal
public final class InventoryEventListener implements Listener {
    private static final Lazy<InventoryEventListener> inst = Lazy.create(InventoryEventListener::new);
    /*
    private static final Field inventoryEvent_transaction;

    static {
        Field field;
        try {
            field = InventoryEvent.class.getDeclaredField("transaction");
        } catch (NoSuchFieldException e) {
            field = null;
            BarleyTeaAPI api = BarleyTeaAPI.getInstanceUnsafe();
            if (api != null) {
                api.getLogger().log(Level.WARNING, "Cannot get transaction field in InventoryEvent!", e);
            }
        }
        if (field != null) {
            field.setAccessible(true);
        }
        inventoryEvent_transaction = field;
    }*/

    private InventoryEventListener() {
    }

    @Nonnull
    public static InventoryEventListener getInstance() {
        return inst.get();
    }

    @Nonnull
    private static MerchantRecipe translateRecipe(@Nonnull MerchantRecipe recipe) {
        var ingredients = recipe.getIngredients().stream()
                .map(InventoryEventListener::restoreItem)
                .toList();
        var oldResult = recipe.getResult();
        var newResult = ObjectUtil.safeMap(restoreItem(oldResult), WithFlag::obj);
        if (newResult != null && newResult != oldResult)
            recipe = new MerchantRecipe(newResult, recipe.getUses(), recipe.getMaxUses(),
                    recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(),
                    recipe.getDemand(), recipe.getSpecialPrice(), recipe.shouldIgnoreDiscounts());
        if (ingredients.stream().anyMatch(flag -> flag != null && flag.flag())) {
            recipe.setIngredients(ingredients.stream()
                    .map(flag -> flag == null ? null : flag.obj())
                    .toList());
        }
        return recipe;
    }

    @Nullable
    private static WithFlag<ItemStack> restoreItem(@Nullable ItemStack itemStack) {
        if (itemStack == null)
            return null;
        if (!ItemHelper.isCustomItem(itemStack))
            return new WithFlag<>(itemStack);
        return new WithFlag<>(AlternativeItemState.restore(itemStack), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemEnchanting(EnchantItemEvent event) {
        if (event == null || event.isCancelled() || !ItemRegister.hasRegistered())
            return;
        ItemStack itemStack = event.getItem();
        CustomItem itemType = CustomItem.get(itemStack);
        if (itemType == null)
            return;
        if (itemType instanceof FeatureItemEnchant feature) {
            DataItemEnchant data = new DataItemEnchant(event);
            feature.handleItemEnchant(data);
            Consumer<ItemStack> job = data.getJobAfterItemEnchant();
            if (job != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(BarleyTeaAPI.getInstance(),
                        () -> job.accept(event.getItem()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemGrinding(PrepareGrindstoneEvent event) {
        if (event == null || !ItemRegister.hasRegistered())
            return;
        ItemStack resultItem = event.getResult();
        if (resultItem == null || resultItem.getType().isAir())
            return;
        final GrindstoneInventory inventory = event.getInventory();
        final ItemStack upperItem = inventory.getUpperItem();
        final ItemStack lowerItem = inventory.getLowerItem();
        if (upperItem != null && lowerItem != null) {
            CustomItem itemType = CustomItem.get(upperItem);
            if (itemType == null) {
                if (ItemHelper.isCustomItem(lowerItem)) {
                    event.setResult(null);
                    return;
                }
            } else {
                final ItemStack oldResultItem = resultItem;
                if (ItemHelper.isCertainItem(itemType, lowerItem)) {
                    resultItem = ItemFeatureLinker.doItemRepair(upperItem, lowerItem, resultItem);
                    if (itemType instanceof FeatureItemGrindstone feature) {
                        if (feature.handleItemGrindstone(new DataItemGrindstone(event))) {
                            resultItem = event.getResult();
                        } else {
                            resultItem = null;
                        }
                    }
                } else {
                    resultItem = null;
                }
                if (oldResultItem != resultItem) {
                    event.setResult(resultItem);
                }
                return;
            }
        } else {
            ItemStack item = upperItem;
            if (item == null)
                item = lowerItem;
            if (item == null)
                return;
            CustomItem itemType = CustomItem.get(item);
            if (itemType != null) {
                final ItemStack oldResultItem = resultItem;
                if (CustomItem.get(item) instanceof FeatureItemGrindstone feature) {
                    if (feature.handleItemGrindstone(new DataItemGrindstone(event))) {
                        resultItem = event.getResult();
                    } else {
                        resultItem = null;
                    }
                }
                if (oldResultItem != resultItem) {
                    event.setResult(resultItem);
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listenItemAnviled(PrepareAnvilEvent event) {
        if (event == null || !ItemRegister.hasRegistered())
            return;
        ItemStack resultItem = event.getResult();
        if (resultItem == null || resultItem.getType().isAir())
            return;
        final AnvilInventory inventory = event.getInventory();
        final ItemStack firstItem = inventory.getFirstItem();
        final ItemStack secondItem = inventory.getSecondItem();
        if (firstItem != null) {
            CustomItem itemType = CustomItem.get(firstItem);
            if (itemType == null) {
                if (ItemHelper.isCustomItem(secondItem)) {
                    event.setResult(null);
                    return;
                }
            } else {
                final ItemStack oldResultItem = resultItem;
                if (ItemHelper.isCertainItem(itemType, secondItem)) {
                    if (itemType instanceof FeatureItemAnvil feature) {
                        if (feature.handleItemAnvilRepair(new DataItemAnvilRepair(event))) {
                            resultItem = event.getResult();
                        } else {
                            resultItem = null;
                        }
                    }
                } else if (ItemHelper.isCertainItem(itemType, resultItem)) {
                    if (itemType instanceof FeatureItemAnvil feature) {
                        if (secondItem != null && !secondItem.getType().isAir()) { //Combine mode
                            if (feature.handleItemAnvilCombine(new DataItemAnvilCombine(event))) {
                                resultItem = event.getResult();
                            } else {
                                resultItem = null;
                            }
                        } else { //Rename Mode
                            if (feature.handleItemAnvilRename(new DataItemAnvilRename(event))) {
                                resultItem = event.getResult();
                            } else {
                                resultItem = null;
                            }
                        }
                    } else if (secondItem != null && firstItem.getType().equals(secondItem.getType())) {
                        resultItem = null;
                    }
                }
                if (oldResultItem != resultItem) {
                    event.setResult(resultItem);
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void listenInventoryClick(InventoryClickEvent event) {
        if (event == null)
            return;
        if (event.getInventory() instanceof MerchantInventory inventory) {
            //Fixes shopkeepers bug
            Merchant merchant = inventory.getMerchant();
            merchant.setRecipes(merchant.getRecipes()
                    .stream().map(InventoryEventListener::translateRecipe)
                    .toList());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void listenTradeSelect(TradeSelectEvent event) {
        if (event == null)
            return;
        //Fixes shopkeepers bug
        Merchant merchant = event.getMerchant();
        merchant.setRecipes(merchant.getRecipes()
                .stream().map(InventoryEventListener::translateRecipe)
                .toList());
    }


/*
    public void listenInventoryClick(InventoryClickEvent event) {
        if (event == null || inventoryEvent_transaction == null)
            return;
        if (event.getInventory() instanceof MerchantInventory inventory) {
            ItemStack[] items = new ItemStack[]{inventory.getItem(0),
                    inventory.getItem(1), inventory.getItem(2)};
            if (Arrays.stream(items).anyMatch(BaseItem::isBarleyTeaItem)) {
                BarleyTeaAPI.getInstance().getLogger().info("A");
                try {
                    if (inventoryEvent_transaction.get(event) instanceof InventoryView view) {
                        WrappedInventoryView wrappedView = WrappedInventoryView.wrap(view);
                        WrappedMerchantInventory wrappedInventory = WrappedMerchantInventory.wrap(inventory);
                        MerchantRecipe recipe = wrappedInventory.getSelectedRecipe();
                        if (recipe == null) {
                            BarleyTeaAPI.getInstance().getLogger().info("B");
                            recipe = searchRecipe(wrappedInventory.getMerchant(),
                                    items, wrappedInventory.getSelectedRecipeIndex());
                            if (recipe == null)
                                return;
                            else
                                inventory.setItem(2, recipe.getResult());
                        }
                        BarleyTeaAPI.getInstance().getLogger().info("C");
                        recipe = translateRecipe(recipe);
                        wrappedInventory.setSelectedRecipe(recipe);
                        wrappedView.setTopInventory(wrappedInventory);
                        inventoryEvent_transaction.set(event, wrappedView);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Nullable
    private static MerchantRecipe searchRecipe(@Nonnull Merchant merchant, @Nonnull ItemStack[] ingredients, int predictedIndex) {
        int count = ingredients.length;
        if (count < 2)
            return null;
        count--;
        ItemStack result = ingredients[count];
        List<ItemStack> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ItemStack itemStack = ingredients[i];
            if (itemStack != null && !itemStack.getType().isAir())
                list.add(itemStack);
        }
        list.removeIf(Objects::isNull);
        List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
        if (predictedIndex >= 0 && predictedIndex < recipes.size()) {
            MerchantRecipe resultRecipe = isRecipeMatches(recipes.get(predictedIndex), list, result);
            if (resultRecipe == null) {
                recipes.remove(predictedIndex);
            } else {
                return resultRecipe;
            }
        }
        for (MerchantRecipe recipe : recipes) {
            MerchantRecipe resultRecipe = isRecipeMatches(recipe, list, result);
            if (resultRecipe != null)
                return resultRecipe;
        }
        return null;
    }

    @Nullable
    private static MerchantRecipe isRecipeMatches(@Nonnull MerchantRecipe recipe, @Nonnull List<ItemStack> ingredients,
                                                  @Nullable ItemStack result) {
        recipe = translateRecipe(recipe);
        if (result != null && !result.isSimilar(recipe.getResult())) {
            return null;
        }
        List<ItemStack> listClone = new ArrayList<>(ingredients);
        List<ItemStack> ingredientsOfRecipe = recipe.getIngredients();
        if (!(ingredientsOfRecipe instanceof ArrayList<ItemStack>))
            ingredientsOfRecipe = new ArrayList<>(ingredientsOfRecipe);
        for (var iterator = ingredientsOfRecipe.listIterator(); iterator.hasNext(); ) {
            ItemStack ingredient = iterator.next();
            if (ingredient == null || ingredient.getType().isAir())
                iterator.remove();
            for (var iterator2 = listClone.listIterator(); iterator2.hasNext(); ) {
                ItemStack comparingItem = iterator2.next();
                if (comparingItem.isSimilar(ingredient)) {
                    iterator.remove();
                    iterator2.remove();
                    break;
                }
            }
        }
        if (listClone.isEmpty() && ingredientsOfRecipe.isEmpty()) {
            return recipe;
        }
        return null;
    }

    private static class WrappedInventoryView extends InventoryView {

        @Nonnull
        private final InventoryView view;

        @Nonnull
        private Inventory topInventory;

        private WrappedInventoryView(@Nonnull InventoryView view) {
            this.view = view;
            topInventory = view.getTopInventory();
        }

        @Nonnull
        public static WrappedInventoryView wrap(@Nonnull InventoryView view) {
            if (view instanceof WrappedInventoryView wrappedView)
                return wrappedView;
            else
                return new WrappedInventoryView(view);
        }

        public void setTopInventory(@Nonnull Inventory inventory) {
            topInventory = inventory;
        }

        @Override
        public @Nonnull Inventory getTopInventory() {
            return topInventory;
        }

        @Override
        public @Nonnull Inventory getBottomInventory() {
            return view.getBottomInventory();
        }

        @Override
        public @Nonnull HumanEntity getPlayer() {
            return view.getPlayer();
        }

        @Override
        public @Nonnull InventoryType getType() {
            return view.getType();
        }

        @Deprecated
        @Override
        public @Nonnull String getTitle() {
            return view.getTitle();
        }

        @Override
        public @Nonnull String getOriginalTitle() {
            return view.getOriginalTitle();
        }

        @Override
        public void setTitle(@Nonnull String title) {
            view.setTitle(title);
        }
    }

    private static class WrappedMerchantInventory implements MerchantInventory {
        @Nonnull
        private final MerchantInventory inventory;

        @Nullable
        private MerchantRecipe recipe;

        private WrappedMerchantInventory(@Nonnull MerchantInventory inventory) {
            this.inventory = inventory;
            this.recipe = inventory.getSelectedRecipe();
        }

        @Nonnull
        public static WrappedMerchantInventory wrap(@Nonnull MerchantInventory inventory) {
            if (inventory instanceof WrappedMerchantInventory wrappedInventory)
                return wrappedInventory;
            else
                return new WrappedMerchantInventory(inventory);
        }

        @Override
        public int getSelectedRecipeIndex() {
            return inventory.getSelectedRecipeIndex();
        }

        @Override
        public @Nullable MerchantRecipe getSelectedRecipe() {
            return recipe;
        }

        public void setSelectedRecipe(@Nullable MerchantRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public @Nonnull Merchant getMerchant() {
            return inventory.getMerchant();
        }

        @Override
        public int getSize() {
            return inventory.getSize();
        }

        @Override
        public int getMaxStackSize() {
            return inventory.getMaxStackSize();
        }

        @Override
        public void setMaxStackSize(int size) {
            inventory.setMaxStackSize(size);
        }

        @Override
        public @Nullable ItemStack getItem(int index) {
            return inventory.getItem(index);
        }

        @Override
        public void setItem(int index, @Nullable ItemStack item) {
            inventory.setItem(index, item);
        }

        @Override
        public @Nonnull HashMap<Integer, ItemStack> addItem(@Nonnull ItemStack... items) throws IllegalArgumentException {
            return inventory.addItem(items);
        }

        @Override
        public @Nonnull HashMap<Integer, ItemStack> removeItem(@Nonnull ItemStack... items) throws IllegalArgumentException {
            return inventory.removeItem(items);
        }

        @Override
        public @Nonnull HashMap<Integer, ItemStack> removeItemAnySlot(@Nonnull ItemStack... items) throws IllegalArgumentException {
            return inventory.removeItemAnySlot(items);
        }

        @Override
        public @Nonnull ItemStack[] getContents() {
            return inventory.getContents();
        }

        @Override
        public void setContents(@Nonnull ItemStack[] items) throws IllegalArgumentException {
            inventory.setContents(items);
        }

        @Override
        public @Nonnull ItemStack[] getStorageContents() {
            return inventory.getStorageContents();
        }

        @Override
        public void setStorageContents(@Nonnull ItemStack[] items) throws IllegalArgumentException {
            inventory.setStorageContents(items);
        }

        @Override
        public boolean contains(@Nonnull Material material) throws IllegalArgumentException {
            return inventory.contains(material);
        }

        @Override
        public boolean contains(@Nullable ItemStack item) {
            return inventory.contains(item);
        }

        @Override
        public boolean contains(@Nonnull Material material, int amount) throws IllegalArgumentException {
            return inventory.contains(material, amount);
        }

        @Override
        public boolean contains(@Nullable ItemStack item, int amount) {
            return inventory.contains(item, amount);
        }

        @Override
        public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
            return inventory.containsAtLeast(item, amount);
        }

        @Override
        public @Nonnull HashMap<Integer, ? extends ItemStack> all(@Nonnull Material material) throws IllegalArgumentException {
            return inventory.all(material);
        }

        @Override
        public @Nonnull HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
            return inventory.all(item);
        }

        @Override
        public int first(@Nonnull Material material) throws IllegalArgumentException {
            return inventory.first(material);
        }

        @Override
        public int first(@Nonnull ItemStack item) {
            return inventory.first(item);
        }

        @Override
        public int firstEmpty() {
            return inventory.firstEmpty();
        }

        @Override
        public boolean isEmpty() {
            return inventory.isEmpty();
        }

        @Override
        public void remove(@Nonnull Material material) throws IllegalArgumentException {
            inventory.remove(material);
        }

        @Override
        public void remove(@Nonnull ItemStack item) {
            inventory.remove(item);
        }

        @Override
        public void clear(int index) {
            inventory.clear(index);
        }

        @Override
        public void clear() {
            inventory.clear();
        }

        @Override
        public int close() {
            return inventory.close();
        }

        @Override
        public @Nonnull List<HumanEntity> getViewers() {
            return inventory.getViewers();
        }

        @Override
        public @Nonnull InventoryType getType() {
            return inventory.getType();
        }

        @Override
        public @Nullable InventoryHolder getHolder() {
            return inventory.getHolder();
        }

        @Override
        public @Nullable InventoryHolder getHolder(boolean useSnapshot) {
            return inventory.getHolder(useSnapshot);
        }

        @Override
        public @Nonnull ListIterator<ItemStack> iterator() {
            return inventory.iterator();
        }

        @Override
        public @Nonnull ListIterator<ItemStack> iterator(int index) {
            return inventory.iterator(index);
        }

        @Override
        public @Nullable Location getLocation() {
            return inventory.getLocation();
        }
    }*/
}
