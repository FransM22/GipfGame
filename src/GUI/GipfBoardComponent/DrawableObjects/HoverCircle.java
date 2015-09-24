package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.GipfBoardComponent.UIval;
import GameLogic.Position;
import javafx.geometry.Pos;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by frans on 22-9-2015.
 */
public class HoverCircle extends Circle {
    public HoverCircle(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Position hoverCirclePosition) {
        super(g2, gipfBoardComponent, new HashSet<>(Arrays.asList(hoverCirclePosition)), UIval.get().hoverCircleSize, UIval.get().hoverFillColor, UIval.get().hoverBorderColor, UIval.get().hoverPositionStroke);
    }
}
