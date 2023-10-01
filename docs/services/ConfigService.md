# ConfigService

> `package me.yellowbear.uwujobs.services`

## Summary

ConfigService makes working with (YAML) configuration files easier and more consistent. It takes care of everything from creating and loading the files themselves, to managing and reloading them to provide your class with specific FileConfiguration object.

> **Warning**
> As of now, one class can only be assigned 1 config. This will be worked on in the future!

## Usage 

To use this service, you need to create your "subscriber" class, and then register it in `onEnable()` method in main class, and create a config to which it will be subscribed.

### Creating subscriber class

Each subscriber class needs to implement a `IConfigurableService` interface, and override `reloadConfig(FileConfiguration file)` method.

```java
package me.yellowbear.uwujobs;

import me.yellowbear.uwujobs.interfaces.IConfigurableService;
import org.bukkit.configuration.file.FileConfiguration;

public class MySubscriberClass implements IConfigurableService {

    private FileConfiguration fileConfiguration;

    @Override
    public void reloadConfig(FileConfiguration file) {
        fileConfiguration = file;
    }
}
```

### Creating the config file

> **Warning**
> Make sure that the file you are refering to is in the resource folder!

```java
@Override
public void onEnable() {
    try {
        ConfigService.registerCustomConfig("myconfig.yml");
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

### Subscribing to the config

```java
@Override
public void onEnable() {
    try {
        ConfigService.registerCustomConfig("myconfig.yml");
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    ConfigService.registerService(new MySubscriberClass(), "myconfig.yml"); //Makes MySubscriberClass subscribe to myconfig.yml
    ConfigService.loadConfigs(); //Reloads all configs (should be only called once in the end of the method!)
}
```

### Reloading configs

Method `ConfigService.loadConfigs();` can be used from any class, at any point in time, to reload all the services registered by ConfigService with the values in the config files.