import Cordova
//
//  TrustlyLightboxViewController.swift
//
// Created by Dan Shields on 2024-04-26
//

import UIKit
import TrustlySDK

protocol TrustlyLightboxViewProtocol {
    func onReturnWithTransactionId(transactionId: String, controller: TrustlyLightboxViewController)
    func onCancelWithTransactionId(transactionId: String?, controller: TrustlyLightboxViewController)
}


class TrustlyLightboxViewController: UIViewController {
    
    var establishData:Dictionary<AnyHashable,Any>?
    var delegate: TrustlyLightboxViewProtocol?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let trustlyLightboxPanel = TrustlyView();
        
//        for (k, v) in establishData! {
//            // print( "PWMB: TrustlyLightboxViewController: \(k) == \(v)")
//        }

        self.view = trustlyLightboxPanel.establish(establishData: self.establishData! , onReturn: {(payWithMyBank, returnParameters)->Void in
                let response = returnParameters as! [String:String]
                self.delegate?.onReturnWithTransactionId(transactionId: response["transactionId"]!, controller: self)
            
            }, onCancel: {(payWithMyBank, returnParameters)->Void in
                let response = returnParameters as! [String:String]
                self.delegate?.onCancelWithTransactionId(transactionId: response["transactionId"], controller: self)
        })
        
    }
    
}
