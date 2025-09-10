package io.arona74.believemodcutter.compat.rei;

import com.google.common.collect.Lists;
import io.arona74.believemodcutter.BelieveModCutterMod;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;

import java.util.List;

public class BelieveModCuttingCategory implements DisplayCategory<BelieveModCuttingDisplay> {

    @Override
    public CategoryIdentifier<? extends BelieveModCuttingDisplay> getCategoryIdentifier() {
        return BelieveModCutterREIPlugin.BELIEVEMOD_CUTTING;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BelieveModCutterMod.BELIEVEMOD_CUTTER_ITEM);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("rei.believemodcutter.believemod_cutting");
    }

    @Override
    public List<Widget> setupDisplay(BelieveModCuttingDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 27, startPoint.y + 4)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 5)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 5))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 4, startPoint.y + 5))
                .entries(display.getInputEntries().get(0)).markInput());
        return widgets;
    }
    
    @Override
    public int getDisplayHeight() {
        return 36;
    }
}