// https://github.com/typelevel/skunk/pull/581

// Copyright (c) 2018-2021 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package puzzle
package services

import skunk.{ Codec, Decoder, Encoder }
import eu.timepit.refined.api.{ RefType, Refined, Validate }

trait RefTypeCodecs:

  def refTypeCodec[T, P, F[_, _]](codecT: Codec[T])(
      implicit
      validate: Validate[T, P],
      refType: RefType[F],
  ): Codec[F[T, P]] = codecT.eimap[F[T, P]](refType.refine[P](_)(validate))(
    refType.unwrap
  )

  def refTypeEncoder[T, P, F[_, _]](writeT: Encoder[T])(implicit refType: RefType[F]): Encoder[F[T, P]] =
    writeT.contramap[F[T, P]](refType.unwrap)

  def refTypeDecoder[T, P, F[_, _]](readT: Decoder[T])(
      implicit
      validate: Validate[T, P],
      refType: RefType[F],
  ): Decoder[F[T, P]] = readT.emap[F[T, P]](refType.refine[P](_)(validate))

object refType extends RefTypeCodecs

trait RefinedCodecs:

  def refinedCodec[T, P](codecT: Codec[T])(
      implicit v: Validate[T, P]
  ): Codec[Refined[T, P]] = refType.refTypeCodec[T, P, Refined](codecT)

  def refinedDecoder[T, P](
      decoderT: Decoder[T]
  )(
      implicit v: Validate[T, P]
  ): Decoder[Refined[T, P]] = refType.refTypeDecoder[T, P, Refined](decoderT)

  def refinedEncoder[T, P](encoderT: Encoder[T]): Encoder[Refined[T, P]] =
    refType.refTypeEncoder[T, P, Refined](encoderT)

object refined extends RefinedCodecs

object Syntax:

  extension [T](c: Codec[T])
    def refine[P](
        implicit v: Validate[T, P]
    ): Codec[Refined[T, P]] = refined.refinedCodec(c)

  implicit class RefineEncoderOps[T](val c: Encoder[T]):
    def refine[P]: Encoder[Refined[T, P]] = refined.refinedEncoder(c)

  implicit class RefineDecoderOps[T](val c: Decoder[T]):

    def refine[P](
        implicit v: Validate[T, P]
    ): Decoder[Refined[T, P]] = refined.refinedDecoder(c)
