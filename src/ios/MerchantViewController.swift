import UIKit
import PayWithMyBank

class MerchantViewController: UIViewController {

    var payWithMyBankView = PayWithMyBankView()
    var establishData:Dictionary<AnyHashable,Any> = [:]
    var payWithMyBankPanel = PayWithMyBankView()
    
    var delegate: PayWithMyBankViewProtocol?

    override func viewDidLoad() {
        super.viewDidLoad()

        self.payWithMyBankView.onChangeListener { (eventName, attributes) in
            if let event = eventName, let data = attributes {
                // print("PWMB: MerchantViewController: onChangeListener: \(event) \(data)")
            }
        }
        self.view = self.payWithMyBankView

        var methodToCall = "selectBankWidget"
        if self.establishData["_methodToCall"] as! String == "establish" {
            methodToCall = "establish"
        }
        self.establishData.removeValue( forKey: "_methodToCall")

        if( methodToCall == "selectBankWidget") {        
            print( "PWMB: MerchantViewController.swift: executing selectBankWidget( ...)")
            let _ = self.payWithMyBankView.selectBankWidget( self.establishData) { (view, data) in
                if let data = data {
                    // print("PWMB: MerchantViewController: returnParameters:\(data)")
                    self.establishData = data
                    self.pay()
                }
            }
        } else {
            print( "PWMB: MerchantViewController.swift: executing establish( ...)")
            let _ = self.payWithMyBankView.establish( self.establishData,
                                                      onReturn: {(payWithMyBank, returnParameters)->Void in
                print( "PWMB: returnParameters \(String(describing: returnParameters))")
                self.dismiss( animated: true)
                let transactionId = returnParameters?["transactionId"]
                if let transactionId = transactionId {
                    self.delegate?.onReturnWithTransactionId(transactionId: transactionId)
                }

            }, onCancel: {(payWithMyBank, returnParameters)->Void in
                print( "PWMB: MerchantViewController.swift: payWithMyBankView.establish onCancel() callback")
                self.dismiss( animated: true)
            })
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func pay() {

        let trustlyLightboxViewController = TrustlyLightboxViewController()
        trustlyLightboxViewController.delegate = self
        trustlyLightboxViewController.establishData = establishData
        self.present(trustlyLightboxViewController, animated: true)
    }
    
}

extension MerchantViewController: TrustlyLightboxViewProtocol {
    
    func onReturnWithTransactionId(transactionId: String, controller: TrustlyLightboxViewController) {
        controller.dismiss(animated: true)
        self.delegate?.onReturnWithTransactionId(transactionId: transactionId)
    }
    
    func onCancelWithTransactionId(transactionId: String?, controller: TrustlyLightboxViewController) {
        controller.dismiss(animated: true)
        self.delegate?.onCancelWithTransactionId(transactionId: transactionId)
    }

}

