/* 
 * HaoRan ImageFilter Classes v0.3
 * Copyright (C) 2012 Zhenjun Dai
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation.
 */

package com.idongler.imagefilter.effect;

import com.idongler.imagefilter.IImageFilter;
import com.idongler.imagefilter.Image;


public class SoftGlowFilter implements IImageFilter {

    BrightContrastFilter contrastFx = new BrightContrastFilter();

    GaussianBlurFilter gaussianBlurFx = new GaussianBlurFilter();

    public SoftGlowFilter() {
        contrastFx.BrightnessFactor = 0.1f;
        contrastFx.ContrastFactor = 0.1f;
        gaussianBlurFx.Sigma = 10;

    }

    public SoftGlowFilter(int nSigma, float nBrightness, float nContrast) {
        contrastFx.BrightnessFactor = nBrightness;
        contrastFx.ContrastFactor = nContrast;
        gaussianBlurFx.Sigma = nSigma;
    }

    //@Override
    public Image process(Image imageIn) {
        Image clone = imageIn.clone();
        Image blur = gaussianBlurFx.process(imageIn);
        if (blur != imageIn) {
            imageIn.destroy();
            imageIn = null;
        }
        Image brightImageIn = contrastFx.process(blur);
        if (brightImageIn != blur) {
            blur.destroy();
            blur = null;
        }

        int old_r, old_g, old_b, r, g, b;
        for (int x = 0; x < (brightImageIn.getWidth() - 1); x++) {
            for (int y = 0; y < (brightImageIn.getHeight() - 1); y++) {
                old_r = clone.getRComponent(x, y);
                old_g = clone.getGComponent(x, y);
                old_b = clone.getBComponent(x, y);

                r = 255 - (255 - old_r) * (255 - brightImageIn.getRComponent(x, y)) / 255;
                g = 255 - (255 - old_g) * (255 - brightImageIn.getGComponent(x, y)) / 255;
                b = 255 - (255 - old_b) * (255 - brightImageIn.getBComponent(x, y)) / 255;
                brightImageIn.setPixelColor(x, y, r, g, b);
            }
        }
        return brightImageIn;
    }

}
