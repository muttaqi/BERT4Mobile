# BERT4Mobile

BERT4Mobile is a fork of [BERT-NER](https://github.com/kamalkraj/BERT-NER) that includes a BERT-compatible JIT tracer and a Kotlin package to help with NER using Pytorch Mobile

# Requirements

-  `python3`
- `pip3 install -r requirements.txt`

# Training model
See original [BERT-NER](https://github.com/kamalkraj/BERT-NER) repository for details on training the model

# Tracing model
```python
  from bert import Ner
  import torch
  import bert4mobile
  import io
  from pytorch_transformers import BertForSequenceClassification

  model = Ner("path_to_model")

  //example text needed for the trace function
  text = "Steve went to Paris"

  traced_model = bert4mobile.trace_mobile(model, text)
  traced_model.save('traced_model.pt')
```

# Loading and using model
Import the BERT4NER package and PyTorch Mobile packages:
```Kotlin
import org.bert4mobile.*
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.IValue
import org.pytorch.Module.load
```

Now we can use the provided Kotlin functions. Here is an example of using NER with 12 possible labels:
```Kotlin

private fun predict(text: String): String {
  private var vocab = mutableMapOf<String, Long>()
  private lateinit var model: Module

  loadVocab(vocab, this)
  model = load(assetFilePath(this, "script_model.pt"))

  var words = text.split(" ")
  println("65 ${words}")

  var inputs = mutableListOf<Long>()
  var inputMask = mutableListOf<Long>()
  var segments = mutableListOf<Long>()
  var valid = mutableListOf<Long>()

  words.map{

      if (vocab.containsKey(it)) {

          var id = vocab.getOrPut(it) {1}

          inputs.add(id.toLong())
          valid.add(1)
      }

      else {

          inputs.add(vocab.getOrPut("[UNK]") {1})
          valid.add(0)
      }

      inputMask.add(1)
      segments.add(0)
  }

  var initialSize: Int = inputs.size

  inputs.add(102)
  inputMask.add(1)
  segments.add(0)
  valid.add(1)

  inputs.add(0, 101)
  inputMask.add(0, 1)
  segments.add(0, 0)
  valid.add(0, 1)

  while (inputs.size < 100) {

      inputs.add(0)
      inputMask.add(0)
      segments.add(0)
      valid.add(0)
  }

  var size: Int = inputs.size
  var shape: LongArray = arrayOf(1.toLong(), size.toLong()).toLongArray()

  val inputTensor: Tensor = Tensor.fromBlob(inputs.toLongArray(), shape)
  val inputMaskTensor: Tensor = Tensor.fromBlob(inputMask.toLongArray(), shape)
  val segmentTensor: Tensor = Tensor.fromBlob(segments.toLongArray(), shape)
  val validTensor: Tensor = Tensor.fromBlob(valid.toLongArray(), shape)

  var outputTensor: Tensor = model.forward(IValue.from(inputTensor), IValue.from(inputMaskTensor), IValue.from(segmentTensor), IValue.from(validTensor)).toTensor()

  val scores: FloatArray = outputTensor.dataAsFloatArray
  println("70 ${Arrays.toString(scores)}")
  println("120 ${scores.size}")

  val highestScores = maxInEachSegment(scores, initialSize)

  //"label_map": {"1": "O", "2": "B-MISC", "3": "I-MISC", "4": "B-PER", "5": "I-PER", "6": "B-ORG", "7": "I-ORG", "8": "B-LOC", "9": "I-LOC", "10": "[CLS]", "11": "[SEP]"}
  var labelMap = mapOf(1 to "O",
  2 to "B-MISC",
  3 to "I-MISC",
  4 to "B-PER",
  5 to "I-PER",
  6 to "B-ORG",
  7 to "I-ORG",
  8 to "B-LOC",
  9 to "I-LOC",
  10 to  "[CLS]",
  11 to  "[SEP]")

  var out = ""

  highestScores.mapIndexed() {i, p ->

      out += "${words[i]}: ${labelMap.get(p.first)}, "
  }

  out.substring(0, out.length - 2)

  return out
}
```

