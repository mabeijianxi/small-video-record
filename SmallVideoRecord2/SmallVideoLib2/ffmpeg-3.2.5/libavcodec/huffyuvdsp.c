/*
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#include <stdint.h>

#include "config.h"
#include "libavutil/attributes.h"
#include "mathops.h"
#include "huffyuvdsp.h"

// 0x7f7f7f7f or 0x7f7f7f7f7f7f7f7f or whatever, depending on the cpu's native arithmetic size
#define pb_7f (~0UL / 255 * 0x7f)
#define pb_80 (~0UL / 255 * 0x80)

static void add_bytes_c(uint8_t *dst, uint8_t *src, intptr_t w)
{
    long i;

    for (i = 0; i <= w - (int) sizeof(long); i += sizeof(long)) {
        long a = *(long *) (src + i);
        long b = *(long *) (dst + i);
        *(long *) (dst + i) = ((a & pb_7f) + (b & pb_7f)) ^ ((a ^ b) & pb_80);
    }
    for (; i < w; i++)
        dst[i + 0] += src[i + 0];
}

static void add_hfyu_median_pred_c(uint8_t *dst, const uint8_t *src1,
                                   const uint8_t *diff, intptr_t w,
                                   int *left, int *left_top)
{
    int i;
    uint8_t l, lt;

    l  = *left;
    lt = *left_top;

    for (i = 0; i < w; i++) {
        l      = mid_pred(l, src1[i], (l + src1[i] - lt) & 0xFF) + diff[i];
        lt     = src1[i];
        dst[i] = l;
    }

    *left     = l;
    *left_top = lt;
}

static int add_hfyu_left_pred_c(uint8_t *dst, const uint8_t *src, intptr_t w,
                                int acc)
{
    int i;

    for (i = 0; i < w - 1; i++) {
        acc   += src[i];
        dst[i] = acc;
        i++;
        acc   += src[i];
        dst[i] = acc;
    }

    for (; i < w; i++) {
        acc   += src[i];
        dst[i] = acc;
    }

    return acc;
}

static void add_hfyu_left_pred_bgr32_c(uint8_t *dst, const uint8_t *src,
                                       intptr_t w, uint8_t *left)
{
    int i;
    uint8_t r = left[R], g = left[G], b = left[B], a = left[A];

    for (i = 0; i < w; i++) {
        b += src[4 * i + B];
        g += src[4 * i + G];
        r += src[4 * i + R];
        a += src[4 * i + A];

        dst[4 * i + B] = b;
        dst[4 * i + G] = g;
        dst[4 * i + R] = r;
        dst[4 * i + A] = a;
    }

    left[B] = b;
    left[G] = g;
    left[R] = r;
    left[A] = a;
}

av_cold void ff_huffyuvdsp_init(HuffYUVDSPContext *c)
{
    c->add_bytes                = add_bytes_c;
    c->add_hfyu_median_pred     = add_hfyu_median_pred_c;
    c->add_hfyu_left_pred       = add_hfyu_left_pred_c;
    c->add_hfyu_left_pred_bgr32 = add_hfyu_left_pred_bgr32_c;

    if (ARCH_X86)
        ff_huffyuvdsp_init_x86(c);
}
