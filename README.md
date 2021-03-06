# Readability-score in Java

**Readability** is the skill with which a reader can understand a written text. 
In reality, the readability of text is about on its content and its presentation.
Researchers have used diverse factors to measure readability.
They produce an approximate representation of the _US grade level_ needed to understand a text.   

For the implementation of this application, we used these various factors to measure
readability of a text write in _**English language**_:
- Automated Readability Index (ARI);
- Flesch-Kincaid readability tests;
- Simple Measure of Gobbledygook (SMOG);
- Coleman-Liau index.  

To find these scores, we have to find first: number of **_words_**, **_sentences, characters, 
syllables, polysyllables_** and some others measures (like average number of character per 
100 words ...) in the text in question. 
