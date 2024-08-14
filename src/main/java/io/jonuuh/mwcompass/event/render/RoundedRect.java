package io.jonuuh.mwcompass.event.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

class RoundedRect
{
    private final Character id;
    private float centerX;
    private float centerY;
    public final float width;
    public final float height;
    public final float radius;
    public final Color color;

    RoundedRect()
    {
        this.id = null;
        this.centerX = 0.0F;
        this.centerY = 0.0F;
        this.width = 0.0F;
        this.height = 0.0F;
        this.radius = 0.0F;
        this.color = null;
    }

    RoundedRect(Character id, float centerX, float centerY, float width, float height, float radius, Color color)
    {
        this.id = id;
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.color = color;
    }

    RoundedRect(Character id, float centerX, float centerY, float width, float height, float radius, EnumChatFormatting chatFormatting, float opacity)
    {
        if (!chatFormatting.isColor())
        {
            throw new IllegalArgumentException("EnumChatFormatting param should be a color");
        }

        this.id = id;
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.radius = radius;

        int colorCode = Minecraft.getMinecraft().fontRendererObj.getColorCode(chatFormatting.toString().charAt(1));
        float r = (colorCode >> 16) / 255.0F;
        float g = (colorCode >> 8 & 255) / 255.0F;
        float b = (colorCode & 255) / 255.0F;
        this.color = new Color(r, g, b, opacity);
    }

    public Character getId()
    {
        return this.id;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public void setCenterX(float centerX)
    {
        this.centerX = centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }

    public void setCenterY(float centerY)
    {
        this.centerY = centerY;
    }

    /*
      Draws a rounded rectangle
      Given the params, calculates vertices for four corners/quadrants of a circle, counter-clockwise from (1, 0) on a unit circle.
      When the full object is drawn, all vertices are connected, so by spacing the quadrants apart (width and height),
      and drawing them in the right order (google "opengl triangle winding order") a line will be drawn between
      each quadrant, making it a rounded rectangle
    */
    public void draw(int glMode)
    {
        float ninetyDegRad = (float) (Math.PI / 2);

        float width = Math.max(this.width, 0);
        float height = Math.max(this.height, 0);

        float xRight = this.centerX - (width / 2.0F);
        float xLeft = this.centerX + (width / 2.0F);
        float yUp = this.centerY + (height / 2.0F);
        float yDown = this.centerY - (height / 2.0F);

        GL11.glColor4f(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA());
        GL11.glPushMatrix();
        GL11.glBegin(glMode);

        // Each corner is drawn around (x,y) as the center of the circle
        addQuadrantVertices(xRight, yUp, 0, this.radius); // top right
        addQuadrantVertices(xLeft, yUp, ninetyDegRad, this.radius); // top left
        addQuadrantVertices(xLeft, yDown, ninetyDegRad * 2, this.radius); // bottom left
        addQuadrantVertices(xRight, yDown, ninetyDegRad * 3, this.radius); // bottom right

        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void addQuadrantVertices(float centerX, float centerY, float startAngle, float radius)
    {
        if (radius == 0)
        {
            GL11.glVertex2f(centerX, centerY);
            return;
        }

        float ninetyDegRad = (float) (Math.PI / 2);
        int segments = 32;

        for (float angle = startAngle; angle <= startAngle + ninetyDegRad; angle += ninetyDegRad / segments)
        {
            // Using MathHelper because it uses sin & cos tables (should be faster than Math.cos)
            GL11.glVertex2f(centerX - (radius * MathHelper.cos(angle)), centerY + (radius * MathHelper.sin(angle)));
        }
    }
}
