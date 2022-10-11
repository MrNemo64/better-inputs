[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![GPLv3 License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <!---
  <a href="https://github.com/MrNemo64/better-inputs">
    <img src="images/logo.png" alt="Logo" width="96" height="96"/>
  </a>
  --->

  <h3 align="center">BetterInputs</h3>

  <p align="center">
    <!-- TODO: project_description -->
    <br />
    <a href="https://mrnemo64.github.io/better-inputs/"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/MrNemo64/better-inputs/issues/new">Report Bug</a>
    ·
    <a href="https://github.com/MrNemo64/better-inputs/issues/new">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#installation">Installation</a></li>
        <li>
          <a href="#setup">Setup</a>
          <ul>
            <li><a href="#maven">Maven</a></li>
          </ul>
        </li>
        <li>
          <a href="#usage">Usage</a>
          <ul>
            <li><a href="#using-the-api-to-get-input">Using the api to get input</a></li>
            <li><a href="#using-the-api-to-provide-input">Using the api to provide input</a></li>
          </ul>
        </li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

<!-- <img src="images/banner.png" alt="BetterInputs Banner"/> -->
Aiming to make getting input from users easier, better inputs tries to provide a collection of classes to get input from users. With this API we hope that getting input from an user can be as easy as creating a simple command.

### Built With

* [Spigot](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse)
* [LayLib](https://github.com/Lauriichan/LayLib)


<!-- GETTING STARTED -->
## Getting Started

To get a local copy you only need to download it from [Spigot](https://spigotmc.org/)
or if you want to get the Source just fork this repository or download it as zip.

### Installation

To install the plugin you only need to do following steps:
1. Download the plugin
2. Put it into your server's plugin folder
3. Start or reload your server
4. Enjoy the plugin!

### Setup

#### Maven
To get started with maven you first need to setup your environment to get access to the api maven package.
To do that simply go into your `.m2` folder which can be found at `%appData%\..\..\.m2` (Open `Run` on Windows and just paste the path into there and click `Ok`).
Afterwards if the file doesn't exist yet create the file `settings.xml` in the folder.
Then put following stuff into the file:
```XML
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

	<activeProfiles>
		<activeProfile>github</activeProfile>
	</activeProfiles>

	<profiles>
		<profile>
			<id>github</id>
			<repositories>
				<repository>
					<id>central</id>
					<url>https://repo1.maven.org/maven2</url>
				</repository>
				<repository>
					<id>github0</id>
					<url>https://maven.pkg.github.com/MrNemo64/*</url>
					<snapshots>
						<enabled>true</enabled>
					</snapshots>
				</repository>
			</repositories>
		</profile>
	</profiles>

	<servers>
		<server>
			<id>github0</id>
			<username>YOUR_GITHUB_USERNAME</username>
			<password>A_GITHUB_TOKEN</password>
		</server>
	</servers>
</settings>
```
Now replace `YOUR_GITHUB_USERNAME` with your github username.
Then go to your Github account settings and scroll down until you see `Developer settings`.
Go into the `Developer settings` and then click on `Personal access tokens`.
Once you are there click on the `Generate new token` button.
I would recommend you to enter `Maven packages` into the `Note` field and set the epiration to `No expiration`.
Then you need to enable `repo:status` and `read:packages`. 
Once you enabled those click on `Generate Token` at the bottom.
Now github should show you the token, simply copy the token and replace `A_GITHUB_TOKEN` with it.
The token can be used for multiple servers so if you want to have access to another repository hosted on github simply copy the `github0` repository replace `MrNemo64` with the authors' name and copy the `github0` server. Be sure to rename the repository and server id to for example `github1` or something similar (they have to match up).

Once this is setup you can simply add the dependency like this in your `pom.xml`:
```xml
<dependency>
  <groupId>me.nemo_64.betterinputs</groupId>
  <artifactId>api</artifactId>
  <version>0.1.0</version>
</dependency>
```
You can get the [latest version here](https://github.com/MrNemo64/better-inputs/packages/PACKAGE_ID_HERE)

### Usage

#### Using the api to get input
```Java
package me.nemo_64.betterinputs.example;

import me.nemo_64.betterinputs.api.*;
import me.nemo_64.betterinputs.api.input.*;
import me.nemo_64.betterinputs.api.input.modifier.*;
import me.nemo_64.betterinputs.api.platform.*;
import me.nemo_64.betterinputs.api.util.*;
import me.nemo_64.betterinputs.api.util.argument.*;
import me.nemo_64.betterinputs.api.util.registry.*;
import me.nemo_64.betterinputs.api.util.tick.*;

public final class ExampleClass {

  public static void expandedExample(BetterInputs api, Object actor) {
    // Create a input builder
    InputBuilder<String> builder = api.createInput(String.class);
    // Specify the input provider type that you want to use
    // Depedening on what is available you might get a exception later
    // Default types are:
    // - betterinputs:input/chat
    // - betterinputs:input/anvil
    // - betterinputs:input/command_block
    // Please note that the anvil and command_block input only work on
    // 1.16+ as they require some packet trickery, chat however is guaranteed
    // if BetterInputs is installed on your server
    //
    // We will use the anvil input type as it helps to show everything better
    builder.type("betterinputs:input/anvil");
    // Now simply add the actor
    // Please note that you can't just put anything in here
    // If the actor is not available on the platform then it just won't work
    // On bukkit everything that is a CommandSender will work though
    builder.actor(actor);
    // Now you can add parameters
    // All default input types do not need any parameters but you can still provide some
    // Check out the default input types at https://github.com/MrNemo64/better-inputs/tree/master/bukkit-parent/bukkit-core/src/main/java/me/nemo_64/betterinputs/bukkit/input
    builder.param("name", "Our Anvil Input");
    builder.param("item", "Placeholder Text");
    // Now you can add a cancel listener
    // This step is not required and can theoretically be ignored
    builder.cancelListener((provider, reason) -> {
      provider.getActor().sendMessage("Cancelled because '" + reason + "'!");
    });
    // Lastly we can provide a exception handler
    // I would recommend adding the exception handler but it's not required
    builder.execeptionHandler((throwable) -> {
      // TODO: Handle exception here
    });
    // Now that our builder is fully configured we can get our InputProvider
    // Calling InputBuilder.provider() might cause some exceptions to arise if you made a mistake
    // This includes:
    // - Providing an invalid actor or no actor at all
    // - Trying to get a not valid input provider type
    InputProvider<String> provider = builder.provide();
    // Now that the process is created we can do a couple other things before starting it
    // Like adding modifiers
    // The timeout modifier will just cancel the process once the time is up
    // It works with ticks and therefore uses a class called TickUnit which can convert all
    // provided time units to ticks
    provider.withModifier(new TimeoutModifier(30, TickUnit.SECOND));
    // There is also the attempt modifier which has to be implemented by the input provider
    // The attempt modifier is there to validate the if a input attempt is acceptable
    // If the X attempts (in this case 5) were not acceptable it will cancel the input process
    // The last argument of this modifier is optional and can be removed
    // Its only purpose is to customize the message sent to the owning actor
    // There is a default function for it but null is not accepted
    provider.withModifier(new AttemptModifier(5, (string) -> string.endsWith("!"), (actor) -> actor.sendMessage("Please enter a valid string!")));
    // Lastly we can provide a modifier exception handler
    // Basically a handler for any exception which is thrown by any of our modifiers
    provider.withModifierExceptionHandler((modifier, exception) -> {
      // TODO: Handle modifier exception here
    });
    // Okay now lets start the input process
    // The first call to this function starts it
    // Every other call will just get the future
    StagedFuture<String> future = provider.asFuture();
    // To get the value now simply accept it like this:
    future.thenAccept(string -> {
      // TODO: Do stuff with the value
    });
    // You can theoretically also transform it or work with other different things here at the end
    // We basically used the CompletableFuture class by Java but with small improvements
  }

  public static void compactExample(BetterInputs api, Object actor) {
    // Now lets do this in the more intended way of this api
    // The compact "easy" way
    api.createInput(String.class)
      .actor(actor)
      .type("betterinputs:input/anvil")
      .param("name", "Our Anvil Input")
      .param("item", "Placeholder Text")
      .cancelListener((provider, reason) -> {
        provider.getActor().sendMessage("Cancelled because '" + reason + "'!");
      }).exceptionHandler((throwable) -> {
        // TODO: Handle exception here
      }).provide()
        .withModifier(new TimeoutModifier(30, TickUnit.SECOND))
        // I won't be using a custom message supplier here
        .withModifier(new AttemptModifier(5, (string) -> string.endsWith("!")))
        .withModifierExceptionHandler((modifier, exception) -> {
          // TODO: Handle modifier exception here
        })
        .asFuture().thenAccept((string) -> {
          // TODO: Do stuff with the value
        });
  }

}
```

#### Using the api to provide input

The basics to providing input is done with two classes:
1. The input class itself

```java
package me.nemo_64.betterinputs.example;

import me.nemo_64.betterinputs.api.*;
import me.nemo_64.betterinputs.api.input.*;
import me.nemo_64.betterinputs.api.input.modifier.*;
import me.nemo_64.betterinputs.api.platform.*;
import me.nemo_64.betterinputs.api.util.*;
import me.nemo_64.betterinputs.api.util.argument.*;
import me.nemo_64.betterinputs.api.util.registry.*;
import me.nemo_64.betterinputs.api.util.tick.*;

public final class ExampleInput extends AbstractInput<String> {

  @Override
  protected void onStart(InputProvider<String> provider, IPlatformActor<?> actor){
    // Here the input process is started
    // We recommend doing your start logic here and not in the constructor
    // As everything before here is not guaranteed to be started

    // To complete the input you have to call:
    // completeValue(V); where the value is the type of your input or null (in this case a String)
    // or
    // completeException(Throwable); where the value is an exception
    
    // If you need to access the input provider somewhere that is not in this method
    // then you can just use AbstractInput.provider(); the method is publicly available
    // and works if you well got an input instance so just pass the input instance around I guess
  }

  // This doesn't need to be here
  // But it would be best to add an implementation of it
  // So people can cancel their input process if they just don't need it anymore
  @Override
  protected boolean onCancel() {
    return false; // Default is just disallowing the cancel
  }

}
```

2. The factory class which creates the input class based on the provided arguments


```java
package me.nemo_64.betterinputs.example;

import me.nemo_64.betterinputs.api.*;
import me.nemo_64.betterinputs.api.input.*;
import me.nemo_64.betterinputs.api.input.modifier.*;
import me.nemo_64.betterinputs.api.platform.*;
import me.nemo_64.betterinputs.api.util.*;
import me.nemo_64.betterinputs.api.util.argument.*;
import me.nemo_64.betterinputs.api.util.registry.*;
import me.nemo_64.betterinputs.api.util.tick.*;

public final class ExampleInputFactory extends InputFactory<String, ExampleInput> {

  public ExampleInputFactory(IPlatformKey key) {
    super(key, String.class);
    // Here in your constructor you should ready everything up that is required to work with your input.
    // Stuff like registering listeners or similar things should be done here
  }

  // This method notifies the factory that it was unregistered
  // A implementation is not required but if you work with for example listeners
  // or other stuff that would be unregistered then this is the place to do it
  @Override
  public void onUnregister(){

  }

  // Here your input is created
  // You are provided all arguments that the other plugin using your input provided you with
  // As well as the actor that is owning the input process
  @Override
  protected ExampleInput provide(IPlatformActor<?> actor, ArgumentMap map){
    return new ExampleInput();
  }

}
```

To register everything you need a platform identifiable object.
For bukkit a platform identifiable object is a plugin.
So here a sample plugin class that provides input:

```java 
package me.nemo_64.betterinputs.example;

import me.nemo_64.betterinputs.api.*;
import me.nemo_64.betterinputs.api.input.*;
import me.nemo_64.betterinputs.api.input.modifier.*;
import me.nemo_64.betterinputs.api.platform.*;
import me.nemo_64.betterinputs.api.util.*;
import me.nemo_64.betterinputs.api.util.argument.*;
import me.nemo_64.betterinputs.api.util.registry.*;
import me.nemo_64.betterinputs.api.util.tick.*;

public final class ExamplePlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    // We recommend using this approach for getting the api
    BetterInputs<?> api = Bukkit.getServicesManager().getRegistration(BetterInputs.class).getProvider();
    // Theoretically you could also use:
    // BetterInputs<?> api = BetterInputs.getPlatform();
    IPlatformKeyProvider keyProvider = api.tryGetKeyProvider(this).get();
    // The key can be anything, really
    api.registerInputFactory(new ExampleInputFactory(keyProvider.getKey("input/example")));
  }

}
```

And about the plugin.yml
```yml
main: me.nemo_64.betterinputs.example.ExamplePlugin
name: BetterInputsExample
authors: [MrNemo64, Lauriichan]
version: 0.1.0
api-version: 1.13
depend: [BetterInputs]
```

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/MrNemo64/better-inputs/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Project Link: [https://github.com/MrNemo64/better-inputs](https://github.com/MrNemo64/better-inputs)





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/MrNemo64/better-inputs.svg?style=flat-square
[contributors-url]: https://github.com/MrNemo64/better-inputs/graphs/contributors
[stars-shield]: https://img.shields.io/github/stars/MrNemo64/better-inputs.svg?style=flat-square
[stars-url]: https://github.com/MrNemo64/better-inputs/stargazers
[issues-shield]: https://img.shields.io/github/issues/MrNemo64/better-inputs.svg?style=flat-square
[issues-url]: https://github.com/MrNemo64/better-inputs/issues
[license-shield]: https://img.shields.io/github/license/MrNemo64/better-inputs.svg?style=flat-square
[license-url]: https://github.com/MrNemo64/better-inputs/blob/master/LICENSE
