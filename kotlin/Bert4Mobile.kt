package org.bert4mobile

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import android.R.string
import android.content.Context
import android.text.TextUtils.split

public fun maxInEachSegment(data: FloatArray, segments: Int): MutableList<Pair<Int, Float>>{

    var ret = mutableListOf<Pair<Int, Float>>()

    var i = 12
    var max: Float = 0f
    var maxI: Int = 0
    while (i < data.size && ret.size < segments) {

        for (j in 1..12) {

            if (data[i] > max) {

                max = data[i]
                maxI = j
            }

            i ++
        }

        ret.add(Pair(maxI, max))
        max = 0f
        maxI = 0
    }

    return ret
}

public fun tokenize(text: String, vocab: MutableMap<String, Long>): MutableList<String> {
    var output_tokens: MutableList<String> = mutableListOf<String>()
    var whitespace_tokens: List<String> = whitespace_tokenize(text)
    //var::iterator ptr

    for (token: String in whitespace_tokens) {
        // cout<<*ptr<<"\n";
        //int len_char_array = token.length();
        // char * char_array = new char [token.length()+1];

        if (token.length > 100)
        {
            output_tokens.add("[UNK]");
            continue;
        }

        // cout<<len_char_array<<'\n';
        var is_bad: Boolean = false;
        var start: Int = 0;
        var sub_tokens: MutableList<String> = mutableListOf<String>()
        while(start < token.length) {
            var end: Int = token.length;
            var cur_substr: String = ""
            while(start < end) {
                var substr: String = ""
                for(c in start..end - 1) {

                    substr += token.substring(c, c + 1)
                }

                if (start > 0) {
                    substr = "##" + substr
                }

                if (vocab.containsKey(substr)) {
                    cur_substr = substr
                    break
                }

                end = end - 1
            }

            if(cur_substr == "") {

                is_bad = true
                break
            }

            sub_tokens.add(cur_substr)
            start = end
        }

        if(is_bad) {

            output_tokens.add("[UNK]")
        }

        else {

            var sub_token_string: String = ""

            for (s in sub_tokens) {

                sub_token_string += s
            }

            output_tokens.add(sub_token_string)
        }
    }
    return output_tokens;
}

public fun whitespace_tokenize(text: String): List<String> {

    var result: List<String> = listOf<String>()
    val delimeter = ' '
    var trimmed = text.trim()

    if (trimmed === "") {
        return result
    }

    result = text.split(delimeter)
    return result
}

public fun assetFilePath(context: Context, assetName: String): String {

    println("65")
    var file: File = File(context.filesDir, assetName)

    println("68")
    if (file.exists() && file.length() > 0) {

        println("71")
        return file.absolutePath
    }

    try {

        println("77")
        var inS: InputStream = context.assets.open(assetName)
        var outS: OutputStream = FileOutputStream(file)

        var buffer: ByteArray = ByteArray(4 * 1024, {i -> 0})
        var read: Int

        read = inS.read(buffer)
        while (read != -1) {

            outS.write(buffer, 0, read)
            read = inS.read(buffer)
        }

        println("91")
        outS.flush()

        return file.absolutePath
    } catch (e: Exception) {

        println("98")
    }

    return ""
}

public fun loadVocab(vocab: MutableMap<String, Long>, context: Context): MutableMap<String, Long> {

    val inS: InputStream = File(assetFilePath(context, "vocab.txt")).inputStream()

    var i: Int =  0
    inS.bufferedReader().use {lines -> lines.forEachLine {

        vocab.put(it, i.toLong())
        i++
    }}

    return vocab
}