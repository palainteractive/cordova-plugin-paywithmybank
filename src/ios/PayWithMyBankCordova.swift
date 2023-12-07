import Cordova
import Foundation
import PayWithMyBank

/**
 * #### Pala Interactive - Internal documentation
 * #### Cordova Plugin - Trustly PayWithMyBank v2.3.0
 * #### author: Dan Shields
  * #### created: 2023-02-17
 */

protocol PayWithMyBankViewProtocol {
    func onReturnWithTransactionId( transactionId: Any);
    func onCancelWithTransactionId( transactionId: Any?);
}

class PayWithMyBankViewController: UIViewController {
    
    var establishData:Dictionary<AnyHashable,Any>?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let payWithMyBankPanel = PayWithMyBankView()
        
        let _ = payWithMyBankPanel.selectBankWidget( self.establishData) { (view,data) in
            if let data = data {
                // print(  "PWMB: selectBankWidget: data: \(data)")
                self.establishData = data
            }
        }

    }
}

@objc(PayWithMyBankCordova)
public class PayWithMyBankCordova: CDVPlugin {
    private var establishData:Dictionary<AnyHashable,Any>?
    var trustly: PayWithMyBankView!
    private var callInProgress: CDVInvokedUrlCommand? = nil
    var APP_DEEP_LINK = "com.palainteractive.stardustcasino.paywithmybank://"

    @objc(selectBankWidget:)
    func selectBankWidget( command: CDVInvokedUrlCommand) {
        self.establishData = ["_methodToCall":"selectBankWidget"]
        return self.selectOrEstablish( command: command)
    }
    @objc(establish:)
    func establish( command: CDVInvokedUrlCommand) {
        self.establishData = ["_methodToCall":"establish"]
        return self.selectOrEstablish( command: command)
    }

    func selectOrEstablish( command: CDVInvokedUrlCommand) {
        self.callInProgress = command

        //
        self.establishData!["metadata.urlScheme"]=APP_DEEP_LINK;
        self.establishData!["metadata.integrationContext"]="InAppBrowser";
        //
        let params:AnyObject = command.arguments[0] as AnyObject;
        
        let keys = params.allKeys
        for key in keys! {
            if key as! String == "cancelUrl" || key as! String == "returnUrl" {
                continue
            }
            if key as! String == "customer" {
                let customerDict = params.value( forKey: key as! String) as! Dictionary<String,Any>
                let customerKeys = customerDict.keys
                for customerKey in customerKeys {
                    if customerKey == "address" {
                        let addressDict = customerDict["address"] as! Dictionary<String,Any>
                        let addressKeys = addressDict.keys
                        for addressKey in addressKeys {
                            let val = addressDict[addressKey]
                            let k = "\(key).\(customerKey).\(addressKey)"
                            self.establishData![ k] = String(describing: val!)
                            // print( "PWMB: selectBankWidget: \(k) == \(String(describing: val!))")
                        }
                    } else {
                        let val = customerDict[customerKey]
                        let k = "\(key).\(customerKey)"
                        self.establishData![ k] = String(describing: val!)
                        // print( "PWMB: selectBankWidget: \(k) == \(String(describing: val!))")
                    }
                }
            }
            let val = params.value( forKey: key as! String)
            if val is String {
                self.establishData![key as! String] = val!
                // print( "PWMB: selectBankWidget: \(key) == \(String(describing: val!))")
            }
        }

        if self.establishData != nil {
            // Check if the key 'env' exists and has the value 'production'
            if self.establishData!["env"] as! String == "production" {
                // Remove the key 'env' from the map
                self.establishData!.removeValue(forKey: "env")
            }

            // // Now iterate through the map and print its contents
            // for (key, value) in self.establishData! {
            //     print("TRUSTLY: \(key) = \(value)")
            // }
        } else {
            // print("TRUSTLY: establishData is nil")
        }




        DispatchQueue.main.async {
            let merchantViewController = MerchantViewController();
            merchantViewController.delegate = PayWithMyBankDelegate(
                viewController: self.viewController!,
                commandDelegate: self.commandDelegate!,
                callInProgress: self.callInProgress!)
            merchantViewController.establishData = self.establishData!;
            self.viewController!.present( merchantViewController, animated: true, completion: nil)
        }
    }
    public class PayWithMyBankDelegate: PayWithMyBankViewProtocol {
        var viewController: UIViewController
        var commandDelegate:CDVCommandDelegate
        var callInProgress:CDVInvokedUrlCommand
        init( viewController: UIViewController, commandDelegate:CDVCommandDelegate, callInProgress:CDVInvokedUrlCommand) {
            self.viewController = viewController
            self.commandDelegate = commandDelegate
            self.callInProgress = callInProgress
        }
        func onReturnWithTransactionId(transactionId: Any) {
            self.viewController.dismiss( animated: true)
            self.commandDelegate.send( CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: "{\"transactionId\": \(transactionId)}"
            ), callbackId: self.callInProgress.callbackId);
        }
        
        func onCancelWithTransactionId(transactionId: Any?) {
            self.viewController.dismiss( animated: true)
            self.commandDelegate.send( CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "{\"error\": \"cancelled\"}"
            ), callbackId:self.callInProgress.callbackId);
        }
    }
    
}


