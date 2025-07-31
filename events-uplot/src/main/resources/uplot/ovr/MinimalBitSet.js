/*
 * Copyright BYOWares
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/** BitSets are packed into arrays of "words."  Currently a word is an integer, which consists of 32 bits, requiring 5 address bits. */
const ADDRESS_BITS_PER_WORD = 5;
const BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
const BIT_INDEX_MASK = BITS_PER_WORD - 1;

/* Used to shift left or right for a partial word mask */
const WORD_MASK = 0xffffffff;

/**
 * Given a bit index, return word index containing it.
 *
 * @param {number} bitIndex a bit index
 * @returns The word index containing the bit index.
 */
MinimalBitSet.wordIndex = function(bitIndex) {
    return bitIndex >>> ADDRESS_BITS_PER_WORD
}

function MinimalBitSet() {
    this.words = [];
    this.wordsInUse = 0;
}

/**
 * Returns the value of the bit with the specified index.
 * The value is `true` if the bit with the index `bitIndex` is currently set in this `MinimalBitSet`;
 * otherwise, the result is `false`.
 *
 * @param {number} bitIndex the bit index
 * @returns the value of the bit with the specified index
 */
MinimalBitSet.prototype.get = function (bitIndex) {
    const wordIndex = MinimalBitSet.wordIndex(bitIndex);
    return (wordIndex < this.wordsInUse)
        && ((this.words[wordIndex] & (1 << bitIndex)) != 0);
}

/**
 * Ensures that the MinimalBitSet has enough words.
 *
 * @param {number} bitIndex
 * @returns The word index containing the bit index.
 */
MinimalBitSet.prototype.ensureCapacity = function (bitIndex) {
    const wordIndex = MinimalBitSet.wordIndex(bitIndex);
    if (wordIndex>=this.wordsInUse) this.wordsInUse = wordIndex+1;
    for (let i = this.words.length; i < wordIndex+1; i++) this.words[i] = 0;
    return wordIndex;
};

/**
 * Sets the bit at the specified index to `true`.
 *
 * @param {number} bitIndex a bit index
 */
MinimalBitSet.prototype.set = function (bitIndex) {
    const wordIndex = this.ensureCapacity(bitIndex);
    this.words[wordIndex] |= 1 << bitIndex;
};

/**
 * Sets the bits from the specified `fromIndex` (inclusive) to the specified `toIndex` (exclusive) to `true`.
 *
 * @param {number} fromIndex index of the first bit to be set
 * @param {number} toIndex index after the last bit to be set
 */
MinimalBitSet.prototype.setRange = function (fromIndex, toIndex) {
    if (fromIndex >= toIndex) return;
    const startWordIndex = MinimalBitSet.wordIndex(fromIndex);
    const endWordIndex = this.ensureCapacity(toIndex-1);

    const firstWordMask = WORD_MASK << fromIndex;
    const lastWordMask  = WORD_MASK >>> -toIndex;
    if (startWordIndex == endWordIndex) {
        // One word
        this.words[startWordIndex] |= (firstWordMask & lastWordMask);
    } else {
        // Handle first word
        this.words[startWordIndex] |= firstWordMask;

        // Handle intermediate words, if any
        for (let i = startWordIndex+1 ; i < endWordIndex ; i++)
            this.words[i] = WORD_MASK;

        // Handle last word
        this.words[endWordIndex] |= lastWordMask;
    }
};

/**
 * Sets the bit at the specified index to the complement of its current value.
 *
 * @param {number} bitIndex the index of the bit to flip
 */
MinimalBitSet.prototype.flip = function (bitIndex) {
    const wordIndex = this.ensureCapacity(bitIndex);
    this.words[wordIndex] ^= 1 << bitIndex;
};

/**
 * Sets each bit from the specified `fromIndex` (inclusive) to the specified `toIndex` (exclusive) to the complement of its current value.
 *
 * @param {number} fromIndex index of the first bit to be set
 * @param {number} toIndex index after the last bit to be set
 */
MinimalBitSet.prototype.flipRange = function (fromIndex, toIndex) {
    if (fromIndex >= toIndex) return;
    const startWordIndex = MinimalBitSet.wordIndex(fromIndex);
    const endWordIndex = this.ensureCapacity(toIndex-1);

    const firstWordMask = WORD_MASK << fromIndex;
    const lastWordMask  = WORD_MASK >>> -toIndex;
    if (startWordIndex == endWordIndex) {
        // One word
        this.words[startWordIndex] ^= (firstWordMask & lastWordMask);
    } else {
        // Handle first word
        this.words[startWordIndex] ^= firstWordMask;

        // Handle intermediate words, if any
        for (let i = startWordIndex+1 ; i < endWordIndex ; i++)
            this.words[i] ^= WORD_MASK;

        // Handle last word
        this.words[endWordIndex] ^= lastWordMask;
    }
};

/**
 * Sets all of the bits in this BitSet to `false`.
 *
 * @param {number} bitIndex a bit index
 */
MinimalBitSet.prototype.clearAll = function () {
    while (this.wordsInUse > 0)
        this.words[--this.wordsInUse] = 0;
};

/**
 * Sets the bit at the specified index to `false`.
 *
 * @param {number} bitIndex a bit index
 */
MinimalBitSet.prototype.clear = function (bitIndex) {
    const wordIndex = this.ensureCapacity(bitIndex);
    this.words[wordIndex] &= ~(1 << bitIndex);
};

/**
 * Sets the bits from the specified `fromIndex` (inclusive) to the specified `toIndex` (exclusive) to `false`.
 *
 * @param {number} fromIndex index of the first bit to be set
 * @param {number} toIndex index after the last bit to be set
 */
MinimalBitSet.prototype.clearRange = function (fromIndex, toIndex) {
    if (fromIndex >= toIndex) return;
    const startWordIndex = MinimalBitSet.wordIndex(fromIndex);
    const endWordIndex = this.ensureCapacity(toIndex-1);

    const firstWordMask = WORD_MASK << fromIndex;
    const lastWordMask  = WORD_MASK >>> -toIndex;
    if (startWordIndex == endWordIndex) {
        // One word
        this.words[startWordIndex] &= ~(firstWordMask & lastWordMask);
    } else {
        // Handle first word
        this.words[startWordIndex] &= ~firstWordMask;

        // Handle intermediate words, if any
        for (let i = startWordIndex+1 ; i < endWordIndex ; i++)
            this.words[i] = 0;

        // Handle last word
        this.words[endWordIndex] &= ~lastWordMask;
    }

    let i;
    for (i = this.wordsInUse-1; i >= 0 && this.words[i] != 0; i--);
    this.wordsInUse = i+1; // The new logical size
};
