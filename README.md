# Better Inputs
Aiming to make getting input from users easyer, better inputs tryes to provide a collection of clases to get input from users.
With this API we hope that getting input from an user can be as easy as doing
```Java
InputProcessManager manager = ... // Class in charge of managing input processes, queueing them and starting them
InputProcessSender<?> sender = ... // Represents the user giving the input
InputProcess<?, Integer> process = InputProcessBuilder.simpleProcess()
  .withSender(sender)
  .withModifier(new ChatBloquingModifier()) // Stops the user from receiving any chat messages while the process runs (probably will exist in the spigot api)
  .withModifier(new TimerModifier(30, TimeUnit.SECONDS)) // The user will only have 30 seconds to provide the input
  .whenStarts((sender) -> sender.sendMessage("What is 3+17?")) // callback for when the process starts
  .withListener(new NumberFromChatListener()) // will listen for messages in chat for numbers
  .createAndQueue(manager);
process.getValue().whenComplete((value, error) -> {
  if(error == null) return; // something went wrong with the process
  if(value == 20) 
    sender.sendMessage("Correct!");
});
```
Looks easy right? We want to launch the API with enough listeners and modifiers so that there is one for every case.

Right now better inputs is unusable as it's not finished. After a lot of time thinking about how to develop this API we came to this point but now we would like to hear other developers ideas. We have created the base classes and interfaces that could be used as the infraestructure for this and documented them a little, we would apreciate if you would check them and tell us about it, ideally in an issue, discussion or a thread that we may have created in forums to show the API, that way others can also coment the idea.

## Some explanation
We'll go somewhat in detail here about what each class does. To make clear why some things are the way they are, right now Better Inputs is being thought as a spigot API, but in the future we want to expand it to other platforms like Discord (JDA and Javacord). An example of things done in the way they are is the `InputProcessSender` class
- **InputProcessSender<R>**: Represents the user sending the input and gives a method to send messages to the user. Is generic as sometimes messages cannot be sent. Ideally the generic type will be a Boolean to tell if the message was sent or not, a CompletableFuture if the message may take time to be sent or simply Void. For example, in Javacord sending a message returns a `CompletableFuture<Message>` but in spigot returns `void`, therefore we consider this generalization to be necessary
- **InputProcess<S, V>**: Represents an input process itself. `S` is the type of the sender (something that implements `InputProcessSender`) and `V` is the type of the final value
- **AbstractInputProcess<S, I, V>**: A half finished implementation of `InputProcess` to show how an implementation of it could look like. `S` and `V` are the same as in `InputProcess` and `I` represents the type of the acepted input. In out example above, `I` would be `String` (the sender sends a chat message, a string) and `V` would be `Integer`, the desired value type
- **InputProcessFailureReason**: Represents reasons as to why an input process could fail
- **InputProcessPriority**: Allows to give priorityes to input processes so higher priority ones run first
- **InputProcessState**: Indicates the states in which an input process may be
- **InputProcessRunnerModifier<I, V>**: `I` and `V` are the same as in `AbstractInputProcess`. As shown in the example this would allow to add extras to the input process. Setting a time limit, giving a maximun amout of attempts, bloquings other messages from reaching the user until the process is finished, etc
- **InputProcessListener\<I>**: `I` is the same as in `AbstractInputProcess`. This would be in charge of collecting the user inputs. This would be, for example, listening for chat messages, actions (clicks for example), etc
- **InputProcessRunner\<P>**: `P` is the type of input process the runner is in charge of running. Initially a process would run itself but this had an issue: processes are queued and stared when they can be started so we need to somehow notify the process and its components (listener and modifiers). At first we thought about providing a callback to the manager for when the process starts or having a `onInputProcessStarted` method on `InputProcess` so when the process starts the manager can call it, but having a separated runner seemed better. The runner would be responsible for notifing the process and its components when the process is started by the manager and notify the components when the process finishes and canceling the process
- **InputProcessManager**: Responsable for handling queues of processes. This is done because two processes can't be simultaneously running for only one user, therefore we need to have a manager starting them when no other process is running
- **DefaultInputProcessManager**: An implementation of InputProcessManager showing how the manager could be implented

## Plans for the future
1. We have the idea of providing several types of processes: simple (shown in the example, just to get an input), multy (to get a secuence of inputs, we have thought about implementing it as an implementation of process that has a list of processes itself), complex (like multy but the next process in the secuence is not in a list, is dinamically generated, somehow)
2. Expanding into other platforms like discord but maybe even more

## Things we would like ideas on
1. *InputProcessRunnerModifier*: Should they be part of the runner or the process?
2. *InputProcessRunnerModifier modifing inputs and values*: Maybe a modifier needs to, for some reason, modify the given input/value before it's used so, should we allow modifiers to modify the input/value? In our current implemenmtation we even allow them to discard them (returning an empty optional on `onInputReceived` and `onValueConverted`). This gives us a problem explained in a coment
  > Should a modifier be able to edit the input and value? Or just be notified of it? For now we'll allow to modify the input and value but what does this really mean? If two modifiers edit the input/value which one do we use? Or do we have a priority system and the returned value is the one given to the next modifier? Do we allow a modifier to even discard an input/value? Like, if it returns an empty optional the input/value is discarded. Could allow some filtering based modifiers...

  [InputProcessRunnerModifier#L35](https://github.com/MrNemo64/better-inputs/blob/933bed955acba153ff59132a0c13627d30c40dd6/api/src/main/java/me/nemo_64/better_inputs/InputProcessRunnerModifier.java#L35)

3. *InputProcessProcessor*: In early stages of the idea we had another class: `InputProcessProcessor`. This class was in charge of three things: Checking if the input is the cancel input (what the user sends to cancel the process), checking if the input is valid (in the example above, we turn strings into integers, what if the user sends 'hello' instead of a number? This class was responsible for checking that), and finally turning the input into the value (converting the string into the integer). This acted like a bridge between listeners and processes so it didn't matter if the listener provides strings and the process requires integers, booleans or whatever, you just needed a processor to handle the conversion
4. *Should `InputProcessFailureReason` implement `Throwable`*? So i t can be the error given to the completable future of the process

For now thats all we have to say, thank you for your time!
