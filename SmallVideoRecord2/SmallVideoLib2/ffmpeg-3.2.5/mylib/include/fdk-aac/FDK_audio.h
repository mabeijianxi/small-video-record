
/* -----------------------------------------------------------------------------------------------------------
Software License for The Fraunhofer FDK AAC Codec Library for Android

� Copyright  1995 - 2015 Fraunhofer-Gesellschaft zur F�rderung der angewandten Forschung e.V.
  All rights reserved.

 1.    INTRODUCTION
The Fraunhofer FDK AAC Codec Library for Android ("FDK AAC Codec") is software that implements
the MPEG Advanced Audio Coding ("AAC") encoding and decoding scheme for digital audio.
This FDK AAC Codec software is intended to be used on a wide variety of Android devices.

AAC's HE-AAC and HE-AAC v2 versions are regarded as today's most efficient general perceptual
audio codecs. AAC-ELD is considered the best-performing full-bandwidth communications codec by
independent studies and is widely deployed. AAC has been standardized by ISO and IEC as part
of the MPEG specifications.

Patent licenses for necessary patent claims for the FDK AAC Codec (including those of Fraunhofer)
may be obtained through Via Licensing (www.vialicensing.com) or through the respective patent owners
individually for the purpose of encoding or decoding bit streams in products that are compliant with
the ISO/IEC MPEG audio standards. Please note that most manufacturers of Android devices already license
these patent claims through Via Licensing or directly from the patent owners, and therefore FDK AAC Codec
software may already be covered under those patent licenses when it is used for those licensed purposes only.

Commercially-licensed AAC software libraries, including floating-point versions with enhanced sound quality,
are also available from Fraunhofer. Users are encouraged to check the Fraunhofer website for additional
applications information and documentation.

2.    COPYRIGHT LICENSE

Redistribution and use in source and binary forms, with or without modification, are permitted without
payment of copyright license fees provided that you satisfy the following conditions:

You must retain the complete text of this software license in redistributions of the FDK AAC Codec or
your modifications thereto in source code form.

You must retain the complete text of this software license in the documentation and/or other materials
provided with redistributions of the FDK AAC Codec or your modifications thereto in binary form.
You must make available free of charge copies of the complete source code of the FDK AAC Codec and your
modifications thereto to recipients of copies in binary form.

The name of Fraunhofer may not be used to endorse or promote products derived from this library without
prior written permission.

You may not charge copyright license fees for anyone to use, copy or distribute the FDK AAC Codec
software or your modifications thereto.

Your modified versions of the FDK AAC Codec must carry prominent notices stating that you changed the software
and the date of any change. For modified versions of the FDK AAC Codec, the term
"Fraunhofer FDK AAC Codec Library for Android" must be replaced by the term
"Third-Party Modified Version of the Fraunhofer FDK AAC Codec Library for Android."

3.    NO PATENT LICENSE

NO EXPRESS OR IMPLIED LICENSES TO ANY PATENT CLAIMS, including without limitation the patents of Fraunhofer,
ARE GRANTED BY THIS SOFTWARE LICENSE. Fraunhofer provides no warranty of patent non-infringement with
respect to this software.

You may use this FDK AAC Codec software or modifications thereto only for purposes that are authorized
by appropriate patent licenses.

4.    DISCLAIMER

This FDK AAC Codec software is provided by Fraunhofer on behalf of the copyright holders and contributors
"AS IS" and WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, including but not limited to the implied warranties
of merchantability and fitness for a particular purpose. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE for any direct, indirect, incidental, special, exemplary, or consequential damages,
including but not limited to procurement of substitute goods or services; loss of use, data, or profits,
or business interruption, however caused and on any theory of liability, whether in contract, strict
liability, or tort (including negligence), arising in any way out of the use of this software, even if
advised of the possibility of such damage.

5.    CONTACT INFORMATION

Fraunhofer Institute for Integrated Circuits IIS
Attention: Audio and Multimedia Departments - FDK AAC LL
Am Wolfsmantel 33
91058 Erlangen, Germany

www.iis.fraunhofer.de/amm
amm-info@iis.fraunhofer.de
----------------------------------------------------------------------------------------------------------- */

/**************************  Fraunhofer IIS FDK SysLib  **********************

   Author(s):   Manuel Jander

******************************************************************************/

/** \file   FDK_audio.h
 *  \brief  Global audio struct and constant definitions.
 */

#ifndef FDK_AUDIO_H
#define FDK_AUDIO_H

#include "machine_type.h"
#include "genericStds.h"

#ifdef __cplusplus
extern "C"
{
#endif

/**
 * File format identifiers.
 */
typedef enum
{
  FF_UNKNOWN           = -1, /**< Unknown format.        */
  FF_RAW               = 0,  /**< No container, bit stream data conveyed "as is". */

  FF_MP4_3GPP          = 3,  /**< 3GPP file format.      */
  FF_MP4_MP4F          = 4,  /**< MPEG-4 File format.     */

  FF_RAWPACKETS        = 5,  /**< Proprietary raw packet file. */

  FF_DRMCT             = 12  /**< Digital Radio Mondial (DRM30/DRM+) CT proprietary file format. */

} FILE_FORMAT;

/**
 * Transport type identifiers.
 */
typedef enum
{
  TT_UNKNOWN           = -1, /**< Unknown format.            */
  TT_MP4_RAW           = 0,  /**< "as is" access units (packet based since there is obviously no sync layer) */
  TT_MP4_ADIF          = 1,  /**< ADIF bitstream format.     */
  TT_MP4_ADTS          = 2,  /**< ADTS bitstream format.     */

  TT_MP4_LATM_MCP1     = 6,  /**< Audio Mux Elements with muxConfigPresent = 1 */
  TT_MP4_LATM_MCP0     = 7,  /**< Audio Mux Elements with muxConfigPresent = 0, out of band StreamMuxConfig */

  TT_MP4_LOAS          = 10, /**< Audio Sync Stream.         */

  TT_DRM               = 12  /**< Digital Radio Mondial (DRM30/DRM+) bitstream format. */

} TRANSPORT_TYPE;

#define TT_IS_PACKET(x) \
       ( ((x) == TT_MP4_RAW) \
      || ((x) == TT_DRM) \
      || ((x) == TT_MP4_LATM_MCP0) \
      || ((x) == TT_MP4_LATM_MCP1) )

/**
 * Audio Object Type definitions.
 */
typedef enum
{
  AOT_NONE             = -1,
  AOT_NULL_OBJECT      = 0,
  AOT_AAC_MAIN         = 1, /**< Main profile                              */
  AOT_AAC_LC           = 2, /**< Low Complexity object                     */
  AOT_AAC_SSR          = 3,
  AOT_AAC_LTP          = 4,
  AOT_SBR              = 5,
  AOT_AAC_SCAL         = 6,
  AOT_TWIN_VQ          = 7,
  AOT_CELP             = 8,
  AOT_HVXC             = 9,
  AOT_RSVD_10          = 10, /**< (reserved)                                */
  AOT_RSVD_11          = 11, /**< (reserved)                                */
  AOT_TTSI             = 12, /**< TTSI Object                               */
  AOT_MAIN_SYNTH       = 13, /**< Main Synthetic object                     */
  AOT_WAV_TAB_SYNTH    = 14, /**< Wavetable Synthesis object                */
  AOT_GEN_MIDI         = 15, /**< General MIDI object                       */
  AOT_ALG_SYNTH_AUD_FX = 16, /**< Algorithmic Synthesis and Audio FX object */
  AOT_ER_AAC_LC        = 17, /**< Error Resilient(ER) AAC Low Complexity    */
  AOT_RSVD_18          = 18, /**< (reserved)                                */
  AOT_ER_AAC_LTP       = 19, /**< Error Resilient(ER) AAC LTP object        */
  AOT_ER_AAC_SCAL      = 20, /**< Error Resilient(ER) AAC Scalable object   */
  AOT_ER_TWIN_VQ       = 21, /**< Error Resilient(ER) TwinVQ object         */
  AOT_ER_BSAC          = 22, /**< Error Resilient(ER) BSAC object           */
  AOT_ER_AAC_LD        = 23, /**< Error Resilient(ER) AAC LowDelay object   */
  AOT_ER_CELP          = 24, /**< Error Resilient(ER) CELP object           */
  AOT_ER_HVXC          = 25, /**< Error Resilient(ER) HVXC object           */
  AOT_ER_HILN          = 26, /**< Error Resilient(ER) HILN object           */
  AOT_ER_PARA          = 27, /**< Error Resilient(ER) Parametric object     */
  AOT_RSVD_28          = 28, /**< might become SSC                          */
  AOT_PS               = 29, /**< PS, Parametric Stereo (includes SBR)      */
  AOT_MPEGS            = 30, /**< MPEG Surround                             */

  AOT_ESCAPE           = 31, /**< Signal AOT uses more than 5 bits          */

  AOT_MP3ONMP4_L1      = 32, /**< MPEG-Layer1 in mp4                        */
  AOT_MP3ONMP4_L2      = 33, /**< MPEG-Layer2 in mp4                        */
  AOT_MP3ONMP4_L3      = 34, /**< MPEG-Layer3 in mp4                        */
  AOT_RSVD_35          = 35, /**< might become DST                          */
  AOT_RSVD_36          = 36, /**< might become ALS                          */
  AOT_AAC_SLS          = 37, /**< AAC + SLS                                 */
  AOT_SLS              = 38, /**< SLS                                       */
  AOT_ER_AAC_ELD       = 39, /**< AAC Enhanced Low Delay                    */

  AOT_USAC             = 42, /**< USAC                                      */
  AOT_SAOC             = 43, /**< SAOC                                      */
  AOT_LD_MPEGS         = 44, /**< Low Delay MPEG Surround                   */

  /* Pseudo AOTs */
  AOT_DRM_AAC          = 143, /**< Virtual AOT for DRM (ER-AAC-SCAL without SBR)          */
  AOT_DRM_SBR          = 144, /**< Virtual AOT for DRM (ER-AAC-SCAL with SBR)             */
  AOT_DRM_MPEG_PS      = 145  /**< Virtual AOT for DRM (ER-AAC-SCAL with SBR and MPEG-PS) */

} AUDIO_OBJECT_TYPE;

#define CAN_DO_PS(aot) \
  ((aot) == AOT_AAC_LC \
|| (aot) == AOT_SBR \
|| (aot) == AOT_PS \
|| (aot) == AOT_ER_BSAC \
|| (aot) == AOT_DRM_AAC)

#define IS_USAC(aot) \
  ((aot) == AOT_USAC)

#define IS_LOWDELAY(aot) \
  ((aot) == AOT_ER_AAC_LD \
|| (aot) == AOT_ER_AAC_ELD)

/** Channel Mode ( 1-7 equals MPEG channel configurations, others are arbitrary). */
typedef enum {
  MODE_INVALID                  = -1,
  MODE_UNKNOWN                  =  0,
  MODE_1                        =  1,       /**< C */
  MODE_2                        =  2,       /**< L+R */
  MODE_1_2                      =  3,       /**< C, L+R */
  MODE_1_2_1                    =  4,       /**< C, L+R, Rear */
  MODE_1_2_2                    =  5,       /**< C, L+R, LS+RS */
  MODE_1_2_2_1                  =  6,       /**< C, L+R, LS+RS, LFE */
  MODE_1_2_2_2_1                =  7,       /**< C, LC+RC, L+R, LS+RS, LFE */


  MODE_1_1                      = 16,       /**< 2 SCEs (dual mono) */
  MODE_1_1_1_1                  = 17,       /**< 4 SCEs */
  MODE_1_1_1_1_1_1              = 18,       /**< 6 SCEs */
  MODE_1_1_1_1_1_1_1_1          = 19,       /**< 8 SCEs */
  MODE_1_1_1_1_1_1_1_1_1_1_1_1  = 20,       /**< 12 SCEs */

  MODE_2_2                      = 21,       /**< 2 CPEs */
  MODE_2_2_2                    = 22,       /**< 3 CPEs */
  MODE_2_2_2_2                  = 23,       /**< 4 CPEs */
  MODE_2_2_2_2_2_2              = 24,       /**< 6 CPEs */

  MODE_2_1                      = 30,       /**< CPE,SCE (ARIB standard B32) */

  MODE_7_1_REAR_SURROUND        = 33,       /**< C, L+R, LS+RS, Lrear+Rrear, LFE */
  MODE_7_1_FRONT_CENTER         = 34        /**< C, LC+RC, L+R, LS+RS, LFE */

} CHANNEL_MODE;

/**
 * Speaker description tags.
 * Do not change the enumeration values unless it keeps the following segmentation:
 * - Bit 0-3: Horizontal postion (0: none, 1: front, 2: side, 3: back, 4: lfe)
 * - Bit 4-7: Vertical position (0: normal, 1: top, 2: bottom)
 */
typedef enum {
  ACT_NONE         = 0x00,
  ACT_FRONT        = 0x01,  /*!< Front speaker position (at normal height) */
  ACT_SIDE         = 0x02,  /*!< Side speaker position (at normal height) */
  ACT_BACK         = 0x03,  /*!< Back speaker position (at normal height) */
  ACT_LFE          = 0x04,  /*!< Low frequency effect speaker postion (front) */

  ACT_TOP          = 0x10,  /*!< Top speaker area (for combination with speaker positions) */
  ACT_FRONT_TOP    = 0x11,  /*!< Top front speaker = (ACT_FRONT|ACT_TOP) */
  ACT_SIDE_TOP     = 0x12,  /*!< Top side speaker  = (ACT_SIDE |ACT_TOP) */
  ACT_BACK_TOP     = 0x13,  /*!< Top back speaker  = (ACT_BACK |ACT_TOP) */

  ACT_BOTTOM       = 0x20,  /*!< Bottom speaker area (for combination with speaker positions) */
  ACT_FRONT_BOTTOM = 0x21,  /*!< Bottom front speaker = (ACT_FRONT|ACT_BOTTOM) */
  ACT_SIDE_BOTTOM  = 0x22,  /*!< Bottom side speaker  = (ACT_SIDE |ACT_BOTTOM) */
  ACT_BACK_BOTTOM  = 0x23   /*!< Bottom back speaker  = (ACT_BACK |ACT_BOTTOM) */

} AUDIO_CHANNEL_TYPE;

typedef enum
{
  SIG_UNKNOWN                 = -1,
  SIG_IMPLICIT                =  0,
  SIG_EXPLICIT_BW_COMPATIBLE  =  1,
  SIG_EXPLICIT_HIERARCHICAL   =  2

} SBR_PS_SIGNALING;

/**
 * Audio Codec flags.
 */
#define AC_ER_VCB11     0x000001  /*!< aacSectionDataResilienceFlag     flag (from ASC): 1 means use virtual codebooks  */
#define AC_ER_RVLC      0x000002  /*!< aacSpectralDataResilienceFlag     flag (from ASC): 1 means use huffman codeword reordering */
#define AC_ER_HCR       0x000004  /*!< aacSectionDataResilienceFlag     flag (from ASC): 1 means use virtual codebooks  */
#define AC_SCALABLE     0x000008  /*!< AAC Scalable*/
#define AC_ELD          0x000010  /*!< AAC-ELD */
#define AC_LD           0x000020  /*!< AAC-LD */
#define AC_ER           0x000040  /*!< ER syntax */
#define AC_BSAC         0x000080  /*!< BSAC */
#define AC_USAC         0x000100  /*!< USAC */
#define AC_USAC_TW      0x000200  /*!< USAC time warped filter bank is active */
#define AC_USAC_NOISE   0x000400  /*!< USAC noise filling is active */
#define AC_USAC_HBE     0x000800  /*!< USAC harmonic bandwidth extension is active */
#define AC_RSVD50       0x001000  /*!< Rsvd50 */
#define AC_SBR_PRESENT  0x002000  /*!< SBR present flag (from ASC)             */
#define AC_SBRCRC       0x004000  /*!< SBR CRC present flag. Only relevant for AAC-ELD for now. */
#define AC_PS_PRESENT   0x008000  /*!< PS present flag (from ASC or implicit)  */
#define AC_MPS_PRESENT  0x010000  /*!< MPS present flag (from ASC or implicit) */
#define AC_DRM          0x020000  /*!< DRM bit stream syntax */
#define AC_INDEP        0x040000  /*!< Independency flag */
#define AC_MPS_RES      0x080000  /*!< MPS residual individual channel data. */
#define AC_DAB          0x800000  /*!< DAB bit stream syntax */
#define AC_LD_MPS     0x01000000  /*!< Low Delay MPS. */


/* CODER_CONFIG::flags */
#define CC_MPEG_ID         0x00100000
#define CC_IS_BASELAYER    0x00200000
#define CC_PROTECTION      0x00400000
#define CC_SBR             0x00800000
#define CC_SBRCRC          0x00010000
#define CC_RVLC            0x01000000
#define CC_VCB11           0x02000000
#define CC_HCR             0x04000000
#define CC_PSEUDO_SURROUND 0x08000000
#define CC_USAC_NOISE      0x10000000
#define CC_USAC_TW         0x20000000
#define CC_USAC_HBE        0x40000000

/** Generic audio coder configuration structure. */
typedef struct {
  AUDIO_OBJECT_TYPE aot;         /**< Audio Object Type (AOT).           */
  AUDIO_OBJECT_TYPE extAOT;      /**< Extension Audio Object Type (SBR). */
  CHANNEL_MODE channelMode;      /**< Channel mode.                      */
  INT        samplingRate;       /**< Sampling rate.                     */
  INT        extSamplingRate;    /**< Extended samplerate (SBR).         */
  INT        bitRate;            /**< Average bitrate.                   */
  int        samplesPerFrame;    /**< Number of PCM samples per codec frame and audio channel. */
  int        noChannels;         /**< Number of audio channels.          */
  int        bitsFrame;
  int        nSubFrames;         /**< Amount of encoder subframes. 1 means no subframing.                                     */
  int        BSACnumOfSubFrame;  /**< The number of the sub-frames which are grouped and transmitted in a super-frame (BSAC). */
  int        BSAClayerLength;    /**< The average length of the large-step layers in bytes (BSAC).                            */
  UINT       flags;              /**< flags */
  UCHAR      matrixMixdownA;     /**< Matrix mixdown index to put into PCE. Default value 0 means no mixdown coefficient,
                                      valid values are 1-4 which correspond to matrix_mixdown_idx 0-3. */
  UCHAR      headerPeriod;       /**< Frame period for sending in band configuration buffers in the transport layer. */

  UCHAR      stereoConfigIndex; /**< USAC MPS stereo mode */
  UCHAR      sbrMode;           /**< USAC SBR mode */
  SBR_PS_SIGNALING sbrSignaling;/**< 0: implicit signaling, 1: backwards compatible explicit signaling, 2: hierarcical explicit signaling */

  UCHAR      sbrPresent;
  UCHAR      psPresent;
} CODER_CONFIG;

/** MP4 Element IDs. */
typedef enum
{
  ID_NONE = -1,   /**< Invalid Element helper ID.             */
  ID_SCE = 0,     /**< Single Channel Element.                */
  ID_CPE = 1,     /**< Channel Pair Element.                  */
  ID_CCE = 2,     /**< Coupling Channel Element.              */
  ID_LFE = 3,     /**< LFE Channel Element.                   */
  ID_DSE = 4,     /**< Currently one Data Stream Element for ancillary data is supported. */
  ID_PCE = 5,     /**< Program Config Element.                */
  ID_FIL = 6,     /**< Fill Element.                          */
  ID_END = 7,     /**< Arnie (End Element = Terminator).      */
  ID_EXT = 8,     /**< Extension Payload (ER only).           */
  ID_SCAL = 9,    /**< AAC scalable element (ER only).        */
  ID_LAST
} MP4_ELEMENT_ID;

#define IS_CHANNEL_ELEMENT(elementId) \
  ((elementId) == ID_SCE \
|| (elementId) == ID_CPE \
|| (elementId) == ID_LFE)

#define EXT_ID_BITS 4  /**< Size in bits of extension payload type tags. */

/** Extension payload types. */
typedef enum {
  EXT_FIL           = 0x00,
  EXT_FILL_DATA     = 0x01,
  EXT_DATA_ELEMENT  = 0x02,
  EXT_DATA_LENGTH   = 0x03,
  EXT_LDSAC_DATA    = 0x09,
  EXT_SAOC_DATA     = 0x0a,
  EXT_DYNAMIC_RANGE = 0x0b,
  EXT_SAC_DATA      = 0x0c,
  EXT_SBR_DATA      = 0x0d,
  EXT_SBR_DATA_CRC  = 0x0e
} EXT_PAYLOAD_TYPE;


/**
 * Proprietary raw packet file configuration data type identifier.
 */
typedef enum
{
  TC_NOTHING = 0,     /* No configuration available -> in-band configuration.   */
  TC_RAW_ASC,         /* Configuration data field is a raw AudioSpecificConfig. */
  TC_RAW_SMC,         /* Configuration data field is a raw StreamMuxConfig.     */
  TC_RAW_SDC          /* Configuration data field is a raw Drm SDC.             */

} TP_CONFIG_TYPE;

/*
 * ##############################################################################################
 * Library identification and error handling
 * ##############################################################################################
 */
/* \cond */
#define MODULE_ID_MASK  (0x000000ff)
#define MODULE_ID_SHIFT (24)

typedef enum {
  FDK_NONE   = 0,
  FDK_TOOLS  = 1,
  FDK_SYSLIB = 2,
  FDK_AACDEC = 3,
  FDK_AACENC = 4,
  FDK_SBRDEC = 5,
  FDK_SBRENC = 6,
  FDK_TPDEC  = 7,
  FDK_TPENC  = 8,
  FDK_MPSDEC = 9,
  FDK_MPEGFILEREAD  = 10,
  FDK_MPEGFILEWRITE = 11,
  FDK_MP2DEC = 12,
  FDK_DABDEC = 13,
  FDK_DABPARSE = 14,
  FDK_DRMDEC = 15,
  FDK_DRMPARSE = 16,
  FDK_AACLDENC = 17,
  FDK_MP2ENC = 18,
  FDK_MP3ENC = 19,
  FDK_MP3DEC = 20,
  FDK_MP3HEADPHONE = 21,
  FDK_MP3SDEC = 22,
  FDK_MP3SENC = 23,
  FDK_EAEC    = 24,
  FDK_DABENC  = 25,
  FDK_DMBDEC  = 26,
  FDK_FDREVERB = 27,
  FDK_DRMENC  = 28,
  FDK_METADATATRANSCODER = 29,
  FDK_AC3DEC = 30,
  FDK_PCMDMX = 31,

  FDK_MODULE_LAST

} FDK_MODULE_ID;

/* AAC capability flags */
#define CAPF_AAC_LC           0x00000001  /**< Support flag for AAC Low Complexity.                             */
#define CAPF_ER_AAC_LD        0x00000002  /**< Support flag for AAC Low Delay with Error Resilience tools.      */
#define CAPF_ER_AAC_SCAL      0x00000004  /**< Support flag for AAC Scalable.                                   */
#define CAPF_ER_AAC_LC        0x00000008  /**< Support flag for AAC Low Complexity with Error Resilience tools. */
#define CAPF_AAC_480          0x00000010  /**< Support flag for AAC with 480 framelength.  */
#define CAPF_AAC_512          0x00000020  /**< Support flag for AAC with 512 framelength.  */
#define CAPF_AAC_960          0x00000040  /**< Support flag for AAC with 960 framelength.  */
#define CAPF_AAC_1024         0x00000080  /**< Support flag for AAC with 1024 framelength. */
#define CAPF_AAC_HCR          0x00000100  /**< Support flag for AAC with Huffman Codeword Reordering.    */
#define CAPF_AAC_VCB11        0x00000200  /**< Support flag for AAC Virtual Codebook 11.    */
#define CAPF_AAC_RVLC         0x00000400  /**< Support flag for AAC Reversible Variable Length Coding.   */
#define CAPF_AAC_MPEG4        0x00000800  /**< Support flag for MPEG file format.                           */
#define CAPF_AAC_DRC          0x00001000  /**< Support flag for AAC Dynamic Range Control. */
#define CAPF_AAC_CONCEALMENT  0x00002000  /**< Support flag for AAC concealment.           */
#define CAPF_AAC_DRM_BSFORMAT 0x00004000  /**< Support flag for AAC DRM bistream format.                         */
#define CAPF_ER_AAC_ELD       0x00008000  /**< Support flag for AAC Enhanced Low Delay with Error Resilience tools.  */
#define CAPF_ER_AAC_BSAC      0x00010000  /**< Support flag for AAC BSAC.                           */
#define CAPF_AAC_SUPERFRAMING 0x00020000  /**< Support flag for AAC Superframing.                           */

/* Transport capability flags */
#define CAPF_ADTS             0x00000001  /**< Support flag for ADTS transport format.        */
#define CAPF_ADIF             0x00000002  /**< Support flag for ADIF transport format.        */
#define CAPF_LATM             0x00000004  /**< Support flag for LATM transport format.        */
#define CAPF_LOAS             0x00000008  /**< Support flag for LOAS transport format.        */
#define CAPF_RAWPACKETS       0x00000010  /**< Support flag for RAW PACKETS transport format. */
#define CAPF_DRM              0x00000020  /**< Support flag for DRM/DRM+ transport format.    */
#define CAPF_RSVD50           0x00000040  /**< Support flag for RSVD50 transport format       */

/* SBR capability flags */
#define CAPF_SBR_LP           0x00000001  /**< Support flag for SBR Low Power mode.           */
#define CAPF_SBR_HQ           0x00000002  /**< Support flag for SBR High Quality mode.        */
#define CAPF_SBR_DRM_BS       0x00000004  /**< Support flag for                               */
#define CAPF_SBR_CONCEALMENT  0x00000008  /**< Support flag for SBR concealment.              */
#define CAPF_SBR_DRC          0x00000010  /**< Support flag for SBR Dynamic Range Control.    */
#define CAPF_SBR_PS_MPEG      0x00000020  /**< Support flag for MPEG Parametric Stereo.       */
#define CAPF_SBR_PS_DRM       0x00000040  /**< Support flag for DRM Parametric Stereo.        */

/* MP2 encoder capability flags */
#define CAPF_MP2ENC_SS        0x00000001  /**< Support flag for Seamless Switching.           */
#define CAPF_MP2ENC_DAB       0x00000002  /**< Support flag for Layer2 DAB.                   */

/* DAB capability flags */
#define CAPF_DAB_MP2          0x00000001  /**< Support flag for Layer2 DAB.                   */
#define CAPF_DAB_AAC          0x00000002  /**< Support flag for DAB+ (HE-AAC v2).             */
#define CAPF_DAB_PAD          0x00000004  /**< Support flag for PAD extraction.               */
#define CAPF_DAB_DRC          0x00000008  /**< Support flag for Dynamic Range Control.        */
#define CAPF_DAB_SURROUND     0x00000010  /**< Support flag for DAB Surround (MPS).           */

/* DMB capability flags */
#define CAPF_DMB_BSAC         0x00000001  /**< Support flag for ER AAC BSAC.                  */
#define CAPF_DMB_DRC          0x00000008  /**< Support flag for Dynamic Range Control.        */
#define CAPF_DMB_SURROUND     0x00000010  /**< Support flag for DMB Surround (MPS).           */

/* PCM up/downmmix capability flags */
#define CAPF_DMX_BLIND        0x00000001  /**< Support flag for blind downmixing.             */
#define CAPF_DMX_PCE          0x00000002  /**< Support flag for guided downmix with data from MPEG-2/4 Program Config Elements (PCE). */
#define CAPF_DMX_ARIB         0x00000004  /**< Support flag for PCE guided downmix with slightly different equations and levels to fulfill ARIB standard. */
#define CAPF_DMX_DVB          0x00000008  /**< Support flag for guided downmix with data from DVB ancillary data fields. */
#define CAPF_DMX_CH_EXP       0x00000010  /**< Support flag for simple upmixing by dublicating channels or adding zero channels. */
/* \endcond */


/*
 * ##############################################################################################
 * Library versioning
 * ##############################################################################################
 */

/**
 * Convert each member of version numbers to one single numeric version representation.
 * \param lev0  1st level of version number.
 * \param lev1  2nd level of version number.
 * \param lev2  3rd level of version number.
  */
#define LIB_VERSION(lev0, lev1, lev2) ((lev0<<24 & 0xff000000) | \
                                       (lev1<<16 & 0x00ff0000) | \
                                       (lev2<<8  & 0x0000ff00))

/**
 *  Build text string of version.
 */
#define LIB_VERSION_STRING(info) FDKsprintf((info)->versionStr, "%d.%d.%d", (((info)->version >> 24) & 0xff), (((info)->version >> 16) & 0xff), (((info)->version >> 8 ) & 0xff))

/**
 *  Library information.
 */
typedef struct LIB_INFO
{
  const char* title;
  const char* build_date;
  const char* build_time;
  FDK_MODULE_ID module_id;
  INT version;
  UINT  flags;
  char versionStr[32];
} LIB_INFO;

/** Initialize library info. */
static inline void FDKinitLibInfo( LIB_INFO* info )
{
  int i;

  for (i = 0; i < FDK_MODULE_LAST; i++) {
    info[i].module_id = FDK_NONE;
  }
}

/** Aquire supported features of library. */
static inline UINT FDKlibInfo_getCapabilities( const LIB_INFO* info, FDK_MODULE_ID module_id )
{
  int i;

  for (i=0; i<FDK_MODULE_LAST; i++) {
    if (info[i].module_id == module_id) {
      return info[i].flags;
    }
  }
  return 0;
}

/** Search for next free tab. */
static inline INT FDKlibInfo_lookup( const LIB_INFO* info, FDK_MODULE_ID module_id )
{
  int i = -1;

  for (i = 0; i < FDK_MODULE_LAST; i++) {
    if (info[i].module_id == module_id)
      return -1;
    if (info[i].module_id == FDK_NONE)
      break;
  }
  if (i == FDK_MODULE_LAST)
    return -1;

  return i;
}


/*
 * ##############################################################################################
 * Buffer description
 * ##############################################################################################
 */

/**
 *  I/O buffer descriptor.
 */
typedef struct FDK_bufDescr
{
  void  **ppBase;     /*!< Pointer to an array containing buffer base addresses.
                           Set to NULL for buffer requirement info. */
  UINT   *pBufSize;   /*!< Pointer to an array containing the number of elements that can
                           be placed in the specific buffer. */
  UINT   *pEleSize;   /*!< Pointer to an array containing the element size for each buffer
                           in bytes. That is mostly the number returned by the sizeof()
                           operator for the data type used for the specific buffer. */
  UINT   *pBufType;   /*!< Pointer to an array of bit fields containing a description
                           for each buffer. See XXX below for more details.  */
  UINT    numBufs;    /*!< Total number of buffers. */

} FDK_bufDescr;

/**
 * Buffer type description field.
 */
#define FDK_BUF_TYPE_MASK_IO     ( 0x03 << 30 )
#define FDK_BUF_TYPE_MASK_DESCR  ( 0x3F << 16 )
#define FDK_BUF_TYPE_MASK_ID     ( 0xFF )

#define FDK_BUF_TYPE_INPUT       ( 0x1 << 30 )
#define FDK_BUF_TYPE_OUTPUT      ( 0x2 << 30 )

#define FDK_BUF_TYPE_PCM_DATA    ( 0x1 << 16 )
#define FDK_BUF_TYPE_ANC_DATA    ( 0x2 << 16 )
#define FDK_BUF_TYPE_BS_DATA     ( 0x4 << 16 )

#ifdef __cplusplus
}
#endif

#endif /* FDK_AUDIO_H */
