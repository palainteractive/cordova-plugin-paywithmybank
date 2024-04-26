# cordova-plugin-paywithmybank (Trustly)
#### Pala Interactive - Internal documentation
#### author: Dan Shields
#### created: 2023-03-21

# quick overview:

This Cordova plugin is for integration of the Trustly (Pay With My Bank) product with our B2B and Stardust customers.

### status:

Both the Android and iOS implementations are passing local tests.

The web implementation has passed, videos demonstrating iOS and Android ExpiredSplitToken test have been submitted.

### installation in ionic 4:

```bash
npm install --save https://github.com/palainteractive/cordova-plugin-paywithmybank
```

### installation in ionic 2:

```bash
cordova plugin add https://github.com/palainteractive/cordova-plugin-paywithmybank
```

### Configuration in capacitor build scripts (YAML files):

A URL Type must be added to Info.plist that uniquely identifies the app to return to from the banking workflows.

This URL scheme should be constructed out of the app bundle ID with '.trustly' appended to it.  For example, with satardust NJ, the following YAML content is applicable:

```yaml
platforms:
  ios:
    targets:
      App:
        plist:
          replace: false
          entries:
            - CFBundleURLTypes:
              - CFBundleTypeRole: Editor
                CFBundleURLName: 'trustly.return'
                CFBundleURLSchemes: ['com.boydgaming.stardustcasino.trustly']
```

** note: this URL scheme identifier suffix was recently renamed from '.paywithmybank' to '.trustly' to coincide with the deprecation of version 2 of the trustly SDK, and the transition to version 3.   In this repository, the `trustly-ios` branch coincides with this transition. **

### typescript interface (selectBankWidget):

`data` has the same structure as defined by the web implementation.

```typescript
window.cordova.plugins.PayWithMyBank.selectBankWidget( data, () => {
  // success callback
}, (err) => {
  // error callback
});
```


### typescript interface (establish):

In some cases (e.g. ExpiredSplitToken, Truncated or missing Split Token) the user should be asked to authenticate once more, the `establish()` call handles this.

`data` has the same structure as defined by the web implementation.

```typescript
window.cordova.plugins.PayWithMyBank.establish( data, () => {
  // success callback
}, (err) => {
  // error callback
});
```


