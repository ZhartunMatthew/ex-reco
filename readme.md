# exReco

Intent recognition and entities extraction written on Java using Apache OpenNLP.

### Getting started

Intent recognizer and entity extractor is different classes but you also can use them together for recognizing intent adn then for extraction features (entities) from your query. 

#### Intent recognizing

##### Training
Create your own dataset.txt for intent recognition and put it here:


    data/train/intent/dataset.txt 

    
Data format:
    
    <number><separator><query>
    0   hi
    0   hello
    0   greeting
    1   good bye
    1   see you
    
    
Entry of each line:
- number - number of class, that your query correspond
- separator - use tab for separate class number and query
- query - natural language query, that you want to classify

Create file 'intents_mapping.txt' and give name for each class.
    
    data/train/intent/intents_mapping.txt 

Data format:

    <number>::<query>
    0::hello
    1::bye
    
It's necessary give names for each class.
Entry of each line:
- number - number of class, that you defined in your dataset.txt
- query - intent name

##### Validation
Put your own validation set for intent recognition here.
    
    data/train/intent/validate.txt 

Data format:
    
    <query>::<correct-class>
    hello::0
    hi::0
    greet::0
    see you soon::1
    bye::1
Entry of each line:
- query - natural language query, that you want to check
- correct-class - correct value class, that your query correspond

##### Note

Validation step is optional, you can disable validation for intent recognizer.

    IntentRecognizer intentRecognizer = new IntentRecognizer(INTENT_TRAIN_DIRECTORY + "/intents_mapping.txt"); 
    intentRecognizer.train(INTENT_TRAIN_DIRECTORY, true);  // run intent recognizer training with validation
    intentRecognizer.train(INTENT_TRAIN_DIRECTORY, false); // run intent recognizer training without validation
    
#### Entity extraction

##### Training
Create your own dataset.txt for entity extraction and put it here:


    data/train/entity/dataset.txt 

    
Data format:
    
    I want a room on <START:floor> 7 <END> floor
    Book me a room at <START:time> 13:00 <END>
    Need meeting room at <START:time> 18:00 <END> on <START:floor> 9 floor <END>
    I need <START:room> 803 <END> at <START:time> 13:00 <END>

Just wrap your entity like this:

    word word <START:entity_name> entity_value <END> word word <START:second_entity_name> second_entity_value <END>
    
Each line may have any number of entities of have doesn't have entities. 

##### Validation
Put your own validation set for entity extraction here.
    
    data/train/entity/validate.txt 

Data format:
    
    <query>::<entity_name0>=<entity_value0>::<entity_name1>=<entity_value1>::::<entity_name2>=<entity_value2>
    Need a room on 10 floor in 15:00::time=15:00::floor=10
    Book at 15:00 room on 6::time=15:00::floor=6

Just show all correct values of entities, that appears in your query in order to validate model.   
Entry of each line:
- query - natural language query, that you want to check
- separator - use '::' to separate query and entity, or entities

##### Note

Validation step is optional, you can disable validation for intent recognizer.

    EntityExtractor entityExtractor = new EntityExtractor();
    entityExtractor.train(ENTITY_TRAIN_DIRECTORY, true);  // run training with validation
    entityExtractor.train(ENTITY_TRAIN_DIRECTORY, false); // run training without validation
    
#### Run

