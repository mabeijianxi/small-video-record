
/* -----------------------------------------------------------------------------------------------------------
Software License for The Fraunhofer FDK AAC Codec Library for Android

© Copyright  1995 - 2015 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
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

/*****************************  MPEG-4 AAC Decoder  **************************

   Author(s):   Manuel Jander

******************************************************************************/

/**
 * \file   aacdecoder_lib.h
 * \brief  FDK AAC decoder library interface header file.
 *

\page INTRO Introduction

\section SCOPE Scope

This document describes the high-level interface and usage of the ISO/MPEG-2/4 AAC Decoder
library developed by the Fraunhofer Institute for Integrated Circuits (IIS).
Depending on the library configuration, it implements decoding of AAC-LC (Low-Complexity),
HE-AAC (High-Efficiency AAC, v1 and v2), AAC-LD (Low-Delay) and AAC-ELD (Enhanced Low-Delay).

All references to SBR (Spectral Band Replication) are only applicable to HE-AAC and AAC-ELD
versions of the library. All references to PS (Parametric Stereo) are only applicable to
HE-AAC v2 versions of the library.

\section DecoderBasics Decoder Basics

This document can only give a rough overview about the ISO/MPEG-2 and ISO/MPEG-4 AAC audio
coding standard. To understand all the terms in this document, you are encouraged to read
the following documents.

- ISO/IEC 13818-7 (MPEG-2 AAC), which defines the syntax of MPEG-2 AAC audio bitstreams.
- ISO/IEC 14496-3 (MPEG-4 AAC, subpart 1 and 4), which defines the syntax of MPEG-4 AAC audio bitstreams.
- Lutzky, Schuller, Gayer, Kr&auml;mer, Wabnik, "A guideline to audio codec delay", 116th AES Convention, May 8, 2004

MPEG Advanced Audio Coding is based on a time-to-frequency mapping of the signal. The signal
is partitioned into overlapping portions and transformed into frequency domain. The spectral
components are then quantized and coded.\n
An MPEG2 or MPEG4 AAC audio bitstream is composed of frames. Contrary to MPEG-1/2 Layer-3 (mp3),
the length of individual frames is not restricted to a fixed number of bytes, but can take on
any length between 1 and 768 bytes.


\page LIBUSE Library Usage

\section InterfaceDescritpion API Description

All API header files are located in the folder /include of the release package. They are described in
detail in this document. All header files are provided for usage in C/C++ programs. The AAC decoder library
API functions are located at aacdecoder_lib.h.

In binary releases the decoder core resides in statically linkable libraries called for example libAACdec.a,
(Linux) or FDK_aacDec_lib (Microsoft Visual C++).

\section Calling_Sequence Calling Sequence

For decoding of ISO/MPEG-2/4 AAC or HE-AAC v2 bitstreams the following sequence is mandatory. Input read
and output write functions as well as the corresponding open and close functions are left out, since they
may be implemented differently according to the user's specific requirements. The example implementation in
main.cpp uses file-based input/output, and in such case call mpegFileRead_Open() to open an input file and
to allocate memory for the required structures, and the corresponding mpegFileRead_Close() to close opened
files and to de-allocate associated structures. mpegFileRead_Open() tries to detect the bitstream format and
in case of MPEG-4 file format or Raw Packets file format (a Fraunhofer IIS proprietary format) reads the Audio
Specific Config data (ASC). An unsuccessful attempt to recognize the bitstream format requires the user to
provide this information manually. For any other bitstream formats that are usually applicable in streaming
applications, the decoder itself will try to synchronize and parse the given bitstream fragment using the
FDK transport library. Hence, for streaming applications (without file access) this step is not necessary.

-# Call aacDecoder_Open() to open and retrieve a handle to a new AAC decoder instance.
\dontinclude main.cpp
\skipline aacDecoder_Open
-# If out-of-band config data (Audio Specific Config (ASC) or Stream Mux Config (SMC)) is available, call
aacDecoder_ConfigRaw() to pass it to the decoder and before the decoding process starts. If this data is
not available in advance, the decoder will get it from the bitstream  and configure itself while decoding
with aacDecoder_DecodeFrame().
-# Begin decoding loop.
\skipline do {
-# Read data from bitstream file or stream into a client-supplied input buffer ("inBuffer" in main.cpp).
If it is very small like just 4, aacDecoder_DecodeFrame() will
repeatedly return ::AAC_DEC_NOT_ENOUGH_BITS until enough bits were fed by aacDecoder_Fill(). Only read data
when this buffer has completely been processed and is then empty. For file-based input execute
mpegFileRead_Read() or any other implementation with similar functionality.
-# Call aacDecoder_Fill() to fill the decoder's internal bitstream input buffer with the client-supplied
external bitstream input buffer.
\skipline aacDecoder_Fill
-# Call aacDecoder_DecodeFrame() which writes decoded PCM audio data to a client-supplied buffer. It is the
client's responsibility to allocate a buffer which is large enough to hold this output data.
\skipline aacDecoder_DecodeFrame
If the bitstream's configuration (number of channels, sample rate, frame size) is not known in advance, you may
call aacDecoder_GetStreamInfo() to retrieve a structure containing this information and then initialize an audio
output device. In the example main.cpp, if the number of channels or the sample rate has changed since program
start or since the previously decoded frame, the audio output device will be re-initialized. If WAVE file output
is chosen, a new WAVE file for each new configuration will be created.
\skipline aacDecoder_GetStreamInfo
-# Repeat steps 5 to 7 until no data to decode is available anymore, or if an error occured.
\skipline } while
-# Call aacDecoder_Close() to de-allocate all AAC decoder and transport layer structures.
\skipline aacDecoder_Close

\section BufferSystem Buffer System

There are three main buffers in an AAC decoder application. One external input buffer to hold bitstream
data from file I/O or elsewhere, one decoder-internal input buffer, and one to hold the decoded output
PCM sample data, whereas this output buffer may overlap with the external input buffer.

The external input buffer is set in the example framework main.cpp and its size is defined by ::IN_BUF_SIZE.
You may freely choose different sizes here. To feed the data to the decoder-internal input buffer, use the
function aacDecoder_Fill(). This function returns important information about how many bytes in the
external input buffer have not yet been copied into the internal input buffer (variable bytesValid).
Once the external buffer has been fully copied, it can be re-filled again.
In case you want to re-fill it when there are still unprocessed bytes (bytesValid is unequal 0), you
would have to additionally perform a memcpy(), so that just means unnecessary computational overhead
and therefore we recommend to re-fill the buffer only when bytesValid is 0.

\image latex dec_buffer.png "Lifecycle of the external input buffer" width=9cm

The size of the decoder-internal input buffer is set in tpdec_lib.h (see define ::TRANSPORTDEC_INBUF_SIZE).
You may choose a smaller size under the following considerations:

- each input channel requires 768 bytes
- the whole buffer must be of size 2^n

So for example a stereo decoder:

\f[
TRANSPORTDEC\_INBUF\_SIZE = 2 * 768 = 1536 => 2048
\f]

tpdec_lib.h and TRANSPORTDEC_INBUF_SIZE are not part of the decoder's library interface. Therefore
only source-code clients may change this setting. If you received a library release, please ask us and
we can change this in order to meet your memory requirements.

\page OutputFormat Decoder audio output

\section OutputFormatObtaining Obtaining channel mapping information

The decoded audio output format is indicated by a set of variables of the CStreamInfo structure.
While the members sampleRate, frameSize and numChannels might be quite self explaining,
pChannelType and pChannelIndices might require some more detailed explanation.

These two arrays indicate what is each output channel supposed to be. Both array have
CStreamInfo::numChannels cells. Each cell of pChannelType indicates the channel type, described in
the enum ::AUDIO_CHANNEL_TYPE defined in FDK_audio.h. The cells of pChannelIndices indicate the sub index
among the channels starting with 0 among all channels of the same audio channel type.

The indexing scheme is the same as for MPEG-2/4. Thus indices are counted upwards starting from the front
direction (thus a center channel if any, will always be index 0). Then the indices count up, starting always
with the left side, pairwise from front toward back. For detailed explanation, please refer to
ISO/IEC 13818-7:2005(E), chapter 8.5.3.2.

In case a Program Config is included in the audio configuration, the channel mapping described within
it will be adopted.

In case of MPEG-D Surround the channel mapping will follow the same criteria described in ISO/IEC 13818-7:2005(E),
but adding corresponding top channels to the channel types front, side and back, in order to avoid any
loss of information.

\section OutputFormatChange Changing the audio output format

The channel interleaving scheme and the actual channel order can be changed at runtime through the
parameters ::AAC_PCM_OUTPUT_INTERLEAVED and ::AAC_PCM_OUTPUT_CHANNEL_MAPPING. See the description of those
parameters and the decoder library function aacDecoder_SetParam() for more detail.

\section OutputFormatExample Channel mapping examples

The following examples illustrate the location of individual audio samples in the audio buffer that
is passed to aacDecoder_DecodeFrame() and the expected data in the CStreamInfo structure which can be obtained
by calling aacDecoder_GetStreamInfo().

\subsection ExamplesStereo Stereo

In case of ::AAC_PCM_OUTPUT_INTERLEAVED set to 0 and ::AAC_PCM_OUTPUT_CHANNEL_MAPPING set to 1,
a AAC-LC bit stream which has channelConfiguration = 2 in its audio specific config would lead
to the following values in CStreamInfo:

CStreamInfo::numChannels = 2

CStreamInfo::pChannelType = { ::ACT_FRONT, ::ACT_FRONT }

CStreamInfo::pChannelIndices = { 0, 1 }

Since ::AAC_PCM_OUTPUT_INTERLEAVED is set to 0, the audio channels will be located as contiguous blocks
in the output buffer as follows:

\verbatim
  <left sample 0>  <left sample 1>  <left sample 2>  ... <left sample N>
  <right sample 0> <right sample 1> <right sample 2> ... <right sample N>
\endverbatim

Where N equals to CStreamInfo::frameSize .

\subsection ExamplesSurround Surround 5.1

In case of ::AAC_PCM_OUTPUT_INTERLEAVED set to 1 and ::AAC_PCM_OUTPUT_CHANNEL_MAPPING set to 1,
a AAC-LC bit stream which has channelConfiguration = 6 in its audio specific config, would lead
to the following values in CStreamInfo:

CStreamInfo::numChannels = 6

CStreamInfo::pChannelType = { ::ACT_FRONT, ::ACT_FRONT, ::ACT_FRONT, ::ACT_LFE, ::ACT_BACK, ::ACT_BACK }

CStreamInfo::pChannelIndices = { 1, 2, 0, 0, 0, 1 }

Since ::AAC_PCM_OUTPUT_CHANNEL_MAPPING is 1, WAV file channel ordering will be used. For a 5.1 channel
scheme, thus the channels would be: front left, front right, center, LFE, surround left, surround right.
Thus the third channel is the center channel, receiving the index 0. The other front channels are
front left, front right being placed as first and second channels with indices 1 and 2 correspondingly.
There is only one LFE, placed as the fourth channel and index 0. Finally both surround
channels get the type definition ACT_BACK, and the indices 0 and 1.

Since ::AAC_PCM_OUTPUT_INTERLEAVED is set to 1, the audio channels will be placed in the output buffer
as follows:

\verbatim
<front left sample 0> <front right sample 0>
<center sample 0> <LFE sample 0>
<surround left sample 0> <surround right sample 0>

<front left sample 1> <front right sample 1>
<center sample 1> <LFE sample 1>
<surround left sample 1> <surround right sample 1>

...

<front left sample N> <front right sample N>
<center sample N> <LFE sample N>
<surround left sample N> <surround right sample N>
\endverbatim

Where N equals to CStreamInfo::frameSize .

\subsection ExamplesArib ARIB coding mode 2/1

In case of ::AAC_PCM_OUTPUT_INTERLEAVED set to 1 and ::AAC_PCM_OUTPUT_CHANNEL_MAPPING set to 1,
in case of a ARIB bit stream using coding mode 2/1 as described in ARIB STD-B32 Part 2 Version 2.1-E1, page 61,
would lead to the following values in CStreamInfo:

CStreamInfo::numChannels = 3

CStreamInfo::pChannelType = { ::ACT_FRONT, ::ACT_FRONT,:: ACT_BACK }

CStreamInfo::pChannelIndices = { 0, 1, 0 }

The audio channels will be placed as follows in the audio output buffer:

\verbatim
<front left sample 0> <front right sample 0>  <mid surround sample 0>

<front left sample 1> <front right sample 1> <mid surround sample 1>

...

<front left sample N> <front right sample N> <mid surround sample N>

Where N equals to CStreamInfo::frameSize .

\endverbatim

*/

#ifndef AACDECODER_LIB_H
#define AACDECODER_LIB_H

#include "machine_type.h"
#include "FDK_audio.h"

#include "genericStds.h"

#define AACDECODER_LIB_VL0 2
#define AACDECODER_LIB_VL1 5
#define AACDECODER_LIB_VL2 17

/**
 * \brief  AAC decoder error codes.
 */
typedef enum {
  AAC_DEC_OK                             = 0x0000,  /*!< No error occured. Output buffer is valid and error free. */
  AAC_DEC_OUT_OF_MEMORY                  = 0x0002,  /*!< Heap returned NULL pointer. Output buffer is invalid. */
  AAC_DEC_UNKNOWN                        = 0x0005,  /*!< Error condition is of unknown reason, or from a another module. Output buffer is invalid. */

  /* Synchronization errors. Output buffer is invalid. */
  aac_dec_sync_error_start               = 0x1000,
  AAC_DEC_TRANSPORT_SYNC_ERROR           = 0x1001,  /*!< The transport decoder had syncronisation problems. Do not exit decoding. Just feed new
                                                         bitstream data. */
  AAC_DEC_NOT_ENOUGH_BITS                = 0x1002,  /*!< The input buffer ran out of bits. */
  aac_dec_sync_error_end                 = 0x1FFF,

  /* Initialization errors. Output buffer is invalid. */
  aac_dec_init_error_start               = 0x2000,
  AAC_DEC_INVALID_HANDLE                 = 0x2001,  /*!< The handle passed to the function call was invalid (NULL). */
  AAC_DEC_UNSUPPORTED_AOT                = 0x2002,  /*!< The AOT found in the configuration is not supported. */
  AAC_DEC_UNSUPPORTED_FORMAT             = 0x2003,  /*!< The bitstream format is not supported.  */
  AAC_DEC_UNSUPPORTED_ER_FORMAT          = 0x2004,  /*!< The error resilience tool format is not supported. */
  AAC_DEC_UNSUPPORTED_EPCONFIG           = 0x2005,  /*!< The error protection format is not supported. */
  AAC_DEC_UNSUPPORTED_MULTILAYER         = 0x2006,  /*!< More than one layer for AAC scalable is not supported. */
  AAC_DEC_UNSUPPORTED_CHANNELCONFIG      = 0x2007,  /*!< The channel configuration (either number or arrangement) is not supported. */
  AAC_DEC_UNSUPPORTED_SAMPLINGRATE       = 0x2008,  /*!< The sample rate specified in the configuration is not supported. */
  AAC_DEC_INVALID_SBR_CONFIG             = 0x2009,  /*!< The SBR configuration is not supported. */
  AAC_DEC_SET_PARAM_FAIL                 = 0x200A,  /*!< The parameter could not be set. Either the value was out of range or the parameter does
                                                         not exist. */
  AAC_DEC_NEED_TO_RESTART                = 0x200B,  /*!< The decoder needs to be restarted, since the requiered configuration change cannot be
                                                         performed. */
  AAC_DEC_OUTPUT_BUFFER_TOO_SMALL        = 0x200C,  /*!< The provided output buffer is too small. */
  aac_dec_init_error_end                 = 0x2FFF,

  /* Decode errors. Output buffer is valid but concealed. */
  aac_dec_decode_error_start             = 0x4000,
  AAC_DEC_TRANSPORT_ERROR                = 0x4001,  /*!< The transport decoder encountered an unexpected error. */
  AAC_DEC_PARSE_ERROR                    = 0x4002,  /*!< Error while parsing the bitstream. Most probably it is corrupted, or the system crashed. */
  AAC_DEC_UNSUPPORTED_EXTENSION_PAYLOAD  = 0x4003,  /*!< Error while parsing the extension payload of the bitstream. The extension payload type
                                                         found is not supported. */
  AAC_DEC_DECODE_FRAME_ERROR             = 0x4004,  /*!< The parsed bitstream value is out of range. Most probably the bitstream is corrupt, or
                                                         the system crashed. */
  AAC_DEC_CRC_ERROR                      = 0x4005,  /*!< The embedded CRC did not match. */
  AAC_DEC_INVALID_CODE_BOOK              = 0x4006,  /*!< An invalid codebook was signalled. Most probably the bitstream is corrupt, or the system
                                                         crashed. */
  AAC_DEC_UNSUPPORTED_PREDICTION         = 0x4007,  /*!< Predictor found, but not supported in the AAC Low Complexity profile. Most probably the
                                                         bitstream is corrupt, or has a wrong format. */
  AAC_DEC_UNSUPPORTED_CCE                = 0x4008,  /*!< A CCE element was found which is not supported. Most probably the bitstream is corrupt, or
                                                         has a wrong format. */
  AAC_DEC_UNSUPPORTED_LFE                = 0x4009,  /*!< A LFE element was found which is not supported. Most probably the bitstream is corrupt, or
                                                         has a wrong format. */
  AAC_DEC_UNSUPPORTED_GAIN_CONTROL_DATA  = 0x400A,  /*!< Gain control data found but not supported. Most probably the bitstream is corrupt, or has
                                                         a wrong format. */
  AAC_DEC_UNSUPPORTED_SBA                = 0x400B,  /*!< SBA found, but currently not supported in the BSAC profile. */
  AAC_DEC_TNS_READ_ERROR                 = 0x400C,  /*!< Error while reading TNS data. Most probably the bitstream is corrupt or the system
                                                         crashed. */
  AAC_DEC_RVLC_ERROR                     = 0x400D,  /*!< Error while decoding error resillient data. */
  aac_dec_decode_error_end               = 0x4FFF,

  /* Ancillary data errors. Output buffer is valid. */
  aac_dec_anc_data_error_start           = 0x8000,
  AAC_DEC_ANC_DATA_ERROR                 = 0x8001,  /*!< Non severe error concerning the ancillary data handling. */
  AAC_DEC_TOO_SMALL_ANC_BUFFER           = 0x8002,  /*!< The registered ancillary data buffer is too small to receive the parsed data. */
  AAC_DEC_TOO_MANY_ANC_ELEMENTS          = 0x8003,  /*!< More than the allowed number of ancillary data elements should be written to buffer. */
  aac_dec_anc_data_error_end             = 0x8FFF


} AAC_DECODER_ERROR;


/** Macro to identify initialization errors. */
#define IS_INIT_ERROR(err)   ( (((err)>=aac_dec_init_error_start)   && ((err)<=aac_dec_init_error_end))   ? 1 : 0)
/** Macro to identify decode errors. */
#define IS_DECODE_ERROR(err) ( (((err)>=aac_dec_decode_error_start) && ((err)<=aac_dec_decode_error_end)) ? 1 : 0)
/** Macro to identify if the audio output buffer contains valid samples after calling aacDecoder_DecodeFrame(). */
#define IS_OUTPUT_VALID(err) ( ((err) == AAC_DEC_OK) || IS_DECODE_ERROR(err) )

/**
 * \brief AAC decoder setting parameters
 */
typedef enum
{
  AAC_PCM_OUTPUT_INTERLEAVED              = 0x0000,  /*!< PCM output mode (1: interleaved (default); 0: not interleaved). */
  AAC_PCM_DUAL_CHANNEL_OUTPUT_MODE        = 0x0002,  /*!< Defines how the decoder processes two channel signals: \n
                                                          0: Leave both signals as they are (default). \n
                                                          1: Create a dual mono output signal from channel 1. \n
                                                          2: Create a dual mono output signal from channel 2. \n
                                                          3: Create a dual mono output signal by mixing both channels (L' = R' = 0.5*Ch1 + 0.5*Ch2). */
  AAC_PCM_OUTPUT_CHANNEL_MAPPING          = 0x0003,  /*!< Output buffer channel ordering. 0: MPEG PCE style order, 1: WAV file channel order (default). */
  AAC_PCM_LIMITER_ENABLE                  = 0x0004,  /*!< Enable signal level limiting. \n
                                                          -1: Auto-config. Enable limiter for all non-lowdelay configurations by default. \n
                                                           0: Disable limiter in general. \n
                                                           1: Enable limiter always.
                                                          It is recommended to call the decoder with a AACDEC_CLRHIST flag to reset all states when
                                                          the limiter switch is changed explicitly. */
  AAC_PCM_LIMITER_ATTACK_TIME             = 0x0005,  /*!< Signal level limiting attack time in ms.
                                                          Default confguration is 15 ms. Adjustable range from 1 ms to 15 ms. */
  AAC_PCM_LIMITER_RELEAS_TIME             = 0x0006,  /*!< Signal level limiting release time in ms.
                                                          Default configuration is 50 ms. Adjustable time must be larger than 0 ms. */
  AAC_PCM_MIN_OUTPUT_CHANNELS             = 0x0011,  /*!< Minimum number of PCM output channels. If higher than the number of encoded audio channels,
                                                          a simple channel extension is applied. \n
                                                          -1, 0: Disable channel extenstion feature. The decoder output contains the same number of
                                                                 channels as the encoded bitstream. \n
                                                           1:    This value is currently needed only together with the mix-down feature. See
                                                                 ::AAC_PCM_MAX_OUTPUT_CHANNELS and note 2 below. \n
                                                           2:    Encoded mono signals will be duplicated to achieve a 2/0/0.0 channel output
                                                                 configuration. \n
                                                           6:    The decoder trys to reorder encoded signals with less than six channels to achieve
                                                                 a 3/0/2.1 channel output signal. Missing channels will be filled with a zero signal.
                                                                 If reordering is not possible the empty channels will simply be appended. Only
                                                                 available if instance is configured to support multichannel output. \n
                                                           8:    The decoder trys to reorder encoded signals with less than eight channels to
                                                                 achieve a 3/0/4.1 channel output signal. Missing channels will be filled with a
                                                                 zero signal. If reordering is not possible the empty channels will simply be
                                                                 appended. Only available if instance is configured to support multichannel output.\n
                                                          NOTE: \n
                                                            1. The channel signalling (CStreamInfo::pChannelType and CStreamInfo::pChannelIndices)
                                                               will not be modified. Added empty channels will be signalled with channel type
                                                               AUDIO_CHANNEL_TYPE::ACT_NONE. \n
                                                            2. If the parameter value is greater than that of ::AAC_PCM_MAX_OUTPUT_CHANNELS both will
                                                               be set to the same value. \n
                                                            3. This parameter does not affect MPEG Surround processing. */
  AAC_PCM_MAX_OUTPUT_CHANNELS             = 0x0012,  /*!< Maximum number of PCM output channels. If lower than the number of encoded audio channels,
                                                          downmixing is applied accordingly. If dedicated metadata is available in the stream it
                                                          will be used to achieve better mixing results. \n
                                                          -1, 0: Disable downmixing feature. The decoder output contains the same number of channels
                                                                 as the encoded bitstream. \n
                                                           1:    All encoded audio configurations with more than one channel will be mixed down to
                                                                 one mono output signal. \n
                                                           2:    The decoder performs a stereo mix-down if the number encoded audio channels is
                                                                 greater than two. \n
                                                           6:    If the number of encoded audio channels is greater than six the decoder performs a
                                                                 mix-down to meet the target output configuration of 3/0/2.1 channels. Only
                                                                 available if instance is configured to support multichannel output. \n
                                                           8:    This value is currently needed only together with the channel extension feature.
                                                                 See ::AAC_PCM_MIN_OUTPUT_CHANNELS and note 2 below. Only available if instance is
                                                                 configured to support multichannel output. \n
                                                          NOTE: \n
                                                            1. Down-mixing of any seven or eight channel configuration not defined in ISO/IEC 14496-3
                                                               PDAM 4 is not supported by this software version. \n
                                                            2. If the parameter value is greater than zero but smaller than ::AAC_PCM_MIN_OUTPUT_CHANNELS
                                                               both will be set to same value. \n
                                                            3. The operating mode of the MPEG Surround module will be set accordingly. \n
                                                            4. Setting this param with any value will disable the binaural processing of the MPEG
                                                               Surround module (::AAC_MPEGS_BINAURAL_ENABLE=0). */

  AAC_CONCEAL_METHOD                      = 0x0100,  /*!< Error concealment: Processing method. \n
                                                          0: Spectral muting. \n
                                                          1: Noise substitution (see ::CONCEAL_NOISE). \n
                                                          2: Energy interpolation (adds additional signal delay of one frame, see ::CONCEAL_INTER). \n */

  AAC_DRC_BOOST_FACTOR                    = 0x0200,  /*!< Dynamic Range Control: Scaling factor for boosting gain values.
                                                          Defines how the boosting DRC factors (conveyed in the bitstream) will be applied to the
                                                          decoded signal. The valid values range from 0 (don't apply boost factors) to 127 (fully
                                                          apply all boosting factors). */
  AAC_DRC_ATTENUATION_FACTOR              = 0x0201,  /*!< Dynamic Range Control: Scaling factor for attenuating gain values. Same as
                                                          AAC_DRC_BOOST_FACTOR but for attenuating DRC factors. */
  AAC_DRC_REFERENCE_LEVEL                 = 0x0202,  /*!< Dynamic Range Control: Target reference level. Defines the level below full-scale
                                                          (quantized in steps of 0.25dB) to which the output audio signal will be normalized to by
                                                          the DRC module. The valid values range from 0 (full-scale) to 127 (31.75 dB below
                                                          full-scale). The value smaller than 0 switches off normalization. */
  AAC_DRC_HEAVY_COMPRESSION               = 0x0203,  /*!< Dynamic Range Control: En-/Disable DVB specific heavy compression (aka RF mode).
                                                          If set to 1, the decoder will apply the compression values from the DVB specific ancillary
                                                          data field. At the same time the MPEG-4 Dynamic Range Control tool will be disabled. By
                                                          default heavy compression is disabled. */

  AAC_QMF_LOWPOWER                        = 0x0300,  /*!< Quadrature Mirror Filter (QMF) Bank processing mode. \n
                                                          -1: Use internal default. Implies MPEG Surround partially complex accordingly. \n
                                                           0: Use complex QMF data mode. \n
                                                           1: Use real (low power) QMF data mode. \n */

  AAC_MPEGS_ENABLE                        = 0x0500,  /*!< MPEG Surround: Allow/Disable decoding of MPS content. Available only for decoders with MPEG
                                                          Surround support. */

  AAC_TPDEC_CLEAR_BUFFER                  = 0x0603   /*!< Clear internal bit stream buffer of transport layers. The decoder will start decoding
                                                          at new data passed after this event and any previous data is discarded. */

} AACDEC_PARAM;

/**
 * \brief This structure gives information about the currently decoded audio data.
 *        All fields are read-only.
 */
typedef struct
{
  /* These five members are the only really relevant ones for the user.                                                            */
  INT               sampleRate;          /*!< The samplerate in Hz of the fully decoded PCM audio signal (after SBR processing).   */
  INT               frameSize;           /*!< The frame size of the decoded PCM audio signal. \n
                                              1024 or 960 for AAC-LC \n
                                              2048 or 1920 for HE-AAC (v2) \n
                                              512 or 480 for AAC-LD and AAC-ELD                                                    */
  INT               numChannels;         /*!< The number of output audio channels in the decoded and interleaved PCM audio signal. */
  AUDIO_CHANNEL_TYPE *pChannelType;      /*!< Audio channel type of each output audio channel.                                     */
  UCHAR             *pChannelIndices;    /*!< Audio channel index for each output audio channel.
                                               See ISO/IEC 13818-7:2005(E), 8.5.3.2 Explicit channel mapping using a program_config_element() */
  /* Decoder internal members. */
  INT               aacSampleRate;       /*!< Sampling rate in Hz without SBR (from configuration info).                           */
  INT               profile;             /*!< MPEG-2 profile (from file header) (-1: not applicable (e. g. MPEG-4)).               */
  AUDIO_OBJECT_TYPE aot;                 /*!< Audio Object Type (from ASC): is set to the appropriate value for MPEG-2 bitstreams (e. g. 2 for AAC-LC). */
  INT               channelConfig;       /*!< Channel configuration (0: PCE defined, 1: mono, 2: stereo, ...                       */
  INT               bitRate;             /*!< Instantaneous bit rate.                   */
  INT               aacSamplesPerFrame;  /*!< Samples per frame for the AAC core (from ASC). \n
                                              1024 or 960 for AAC-LC \n
                                              512 or 480 for AAC-LD and AAC-ELD         */
  INT               aacNumChannels;      /*!< The number of audio channels after AAC core processing (before PS or MPS processing).
                                              CAUTION: This are not the final number of output channels! */
  AUDIO_OBJECT_TYPE extAot;              /*!< Extension Audio Object Type (from ASC)   */
  INT               extSamplingRate;     /*!< Extension sampling rate in Hz (from ASC) */

  UINT              outputDelay;         /*!< The number of samples the output is additionally delayed by the decoder. */

  UINT              flags;               /*!< Copy of internal flags. Only to be written by the decoder, and only to be read externally. */

  SCHAR             epConfig;            /*!< epConfig level (from ASC): only level 0 supported, -1 means no ER (e. g. AOT=2, MPEG-2 AAC, etc.)  */

  /* Statistics */
  INT               numLostAccessUnits;  /*!< This integer will reflect the estimated amount of lost access units in case aacDecoder_DecodeFrame()
                                              returns AAC_DEC_TRANSPORT_SYNC_ERROR. It will be < 0 if the estimation failed. */

  UINT              numTotalBytes;       /*!< This is the number of total bytes that have passed through the decoder. */
  UINT              numBadBytes;         /*!< This is the number of total bytes that were considered with errors from numTotalBytes. */
  UINT              numTotalAccessUnits; /*!< This is the number of total access units that have passed through the decoder. */
  UINT              numBadAccessUnits;   /*!< This is the number of total access units that were considered with errors from numTotalBytes. */

  /* Metadata */
  SCHAR             drcProgRefLev;       /*!< DRC program reference level. Defines the reference level below full-scale.
                                              It is quantized in steps of 0.25dB. The valid values range from 0 (0 dBFS) to 127 (-31.75 dBFS).
                                              It is used to reflect the average loudness of the audio in LKFS accoring to ITU-R BS 1770.
                                              If no level has been found in the bitstream the value is -1. */
  SCHAR             drcPresMode;         /*!< DRC presentation mode. According to ETSI TS 101 154, this field indicates whether
                                              light (MPEG-4 Dynamic Range Control tool) or heavy compression (DVB heavy compression)
                                              dynamic range control shall take priority on the outputs.
                                              For details, see ETSI TS 101 154, table C.33. Possible values are: \n
                                              -1: No corresponding metadata found in the bitstream \n
                                               0: DRC presentation mode not indicated \n
                                               1: DRC presentation mode 1 \n
                                               2: DRC presentation mode 2 \n
                                               3: Reserved */

} CStreamInfo;


typedef struct AAC_DECODER_INSTANCE *HANDLE_AACDECODER;  /*!< Pointer to a AAC decoder instance. */

#ifdef __cplusplus
extern "C"
{
#endif

/**
 * \brief Initialize ancillary data buffer.
 *
 * \param self    AAC decoder handle.
 * \param buffer  Pointer to (external) ancillary data buffer.
 * \param size    Size of the buffer pointed to by buffer.
 * \return        Error code.
 */
LINKSPEC_H AAC_DECODER_ERROR
aacDecoder_AncDataInit ( HANDLE_AACDECODER self,
                         UCHAR            *buffer,
                         int               size );

/**
 * \brief Get one ancillary data element.
 *
 * \param self   AAC decoder handle.
 * \param index  Index of the ancillary data element to get.
 * \param ptr    Pointer to a buffer receiving a pointer to the requested ancillary data element.
 * \param size   Pointer to a buffer receiving the length of the requested ancillary data element.
 * \return       Error code.
 */
LINKSPEC_H AAC_DECODER_ERROR
aacDecoder_AncDataGet ( HANDLE_AACDECODER self,
                        int               index,
                        UCHAR           **ptr,
                        int              *size );

/**
 * \brief Set one single decoder parameter.
 *
 * \param self   AAC decoder handle.
 * \param param  Parameter to be set.
 * \param value  Parameter value.
 * \return       Error code.
 */
LINKSPEC_H AAC_DECODER_ERROR
aacDecoder_SetParam ( const HANDLE_AACDECODER  self,
                      const AACDEC_PARAM       param,
                      const INT                value );


/**
 * \brief              Get free bytes inside decoder internal buffer
 * \param self    Handle of AAC decoder instance
 * \param pFreeBytes Pointer to variable receving amount of free bytes inside decoder internal buffer
 * \return             Error code
 */
LINKSPEC_H AAC_DECODER_ERROR
aacDecoder_GetFreeBytes ( const HANDLE_AACDECODER  self,
                                            UINT *pFreeBytes);

/**
 * \brief               Open an AAC decoder instance
 * \param transportFmt  The transport type to be used
 * \return              AAC decoder handle
 */
LINKSPEC_H HANDLE_AACDECODER
aacDecoder_Open ( TRANSPORT_TYPE transportFmt, UINT nrOfLayers );

/**
 * \brief Explicitly configure the decoder by passing a raw AudioSpecificConfig (ASC) or a StreamMuxConfig (SMC),
 *  contained in a binary buffer. This is required for MPEG-4 and Raw Packets file format bitstreams
 *  as well as for LATM bitstreams with no in-band SMC. If the transport format is LATM with or without
 *  LOAS, configuration is assumed to be an SMC, for all other file formats an ASC.
 *
 * \param self    AAC decoder handle.
 * \param conf    Pointer to an unsigned char buffer containing the binary configuration buffer (either ASC or SMC).
 * \param length  Length of the configuration buffer in bytes.
 * \return        Error code.
 */
LINKSPEC_H AAC_DECODER_ERROR
aacDecoder_ConfigRaw ( HANDLE_AACDECODER self,
                       UCHAR            *conf[],
                       const UINT        length[] );


/**
 * \brief Fill AAC decoder's internal input buffer with bitstream data from the external input buffer.
 *  The function only copies such data as long as the decoder-internal input buffer is not full.
 *  So it grabs whatever it can from pBuffer and returns information (bytesValid) so that at a
 *  subsequent call of %aacDecoder_Fill(), the right position in pBuffer can be determined to
 *  grab the next data.
 *
 * \param self        AAC decoder handle.
 * \param pBuffer     Pointer to external input buffer.
 * \param bufferSize  Size of external input buffer. This argument is required because decoder-internally
 *                    we need the information to calculate the offset to pBuffer, where the next
 *                    available data is, which is then fed into the decoder-internal buffer (as much
 *                    as possible). Our example framework implementation fills the buffer at pBuffer
 *                    again, once it contains no available valid bytes anymore (meaning bytesValid equal 0).
 * \param bytesValid  Number of bitstream bytes in the external bitstream buffer that have not yet been
 *                    copied into the decoder's internal bitstream buffer by calling this function.
 *                    The value is updated according to the amount of newly copied bytes.
 * \return            Error code.
 */
LINKSPEC_H AAC_DECODER_ERROR
aacDecoder_Fill ( HANDLE_AACDECODER  self,
                  UCHAR             *pBuffer[],
                  const UINT         bufferSize[],
                  UINT              *bytesValid );

#define AACDEC_CONCEAL  1 /*!< Flag for aacDecoder_DecodeFrame(): Trigger the built-in error concealment module \
                                 to generate a substitute signal for one lost frame. New input data will not be
                                 considered. */
#define AACDEC_FLUSH    2 /*!< Flag for aacDecoder_DecodeFrame(): Flush all filterbanks to get all delayed audio \
                                 without having new input data. Thus new input data will not be considered.*/
#define AACDEC_INTR     4 /*!< Flag for aacDecoder_DecodeFrame(): Signal an input bit stream data discontinuity. \
                                 Resync any internals as necessary. */
#define AACDEC_CLRHIST  8 /*!< Flag for aacDecoder_DecodeFrame(): Clear all signal delay lines and history buffers.\
                                 CAUTION: This can cause discontinuities in the output signal. */

/**
 * \brief            Decode one audio frame
 *
 * \param self       AAC decoder handle.
 * \param pTimeData  Pointer to external output buffer where the decoded PCM samples will be stored into.
 * \param flags      Bit field with flags for the decoder: \n
 *                   (flags & AACDEC_CONCEAL) == 1: Do concealment. \n
 *                   (flags & AACDEC_FLUSH) == 2: Discard input data. Flush filter banks (output delayed audio). \n
 *                   (flags & AACDEC_INTR) == 4: Input data is discontinuous. Resynchronize any internals as necessary.
 * \return           Error code.
 */
LINKSPEC_H AAC_DECODER_ERROR
aacDecoder_DecodeFrame ( HANDLE_AACDECODER  self,
                         INT_PCM           *pTimeData,
                         const INT          timeDataSize,
                         const UINT         flags );

/**
 * \brief       De-allocate all resources of an AAC decoder instance.
 *
 * \param self  AAC decoder handle.
 * \return      void
 */
LINKSPEC_H void aacDecoder_Close ( HANDLE_AACDECODER self );

/**
 * \brief       Get CStreamInfo handle from decoder.
 *
 * \param self  AAC decoder handle.
 * \return      Reference to requested CStreamInfo.
 */
LINKSPEC_H CStreamInfo* aacDecoder_GetStreamInfo( HANDLE_AACDECODER self );

/**
 * \brief       Get decoder library info.
 *
 * \param info  Pointer to an allocated LIB_INFO structure.
 * \return      0 on success
 */
LINKSPEC_H INT aacDecoder_GetLibInfo( LIB_INFO *info );


#ifdef __cplusplus
}
#endif

#endif /* AACDECODER_LIB_H */
