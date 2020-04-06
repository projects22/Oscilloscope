/* Oscilloscope
 *  by moty22.co.uk
 */ 

 unsigned char i, out[60], dh, range=0;

void setup()
{

    //baud rate=38400
  UCSR0A = (1<<U2X0);   //fast rate
  UBRR0H = 0;
  UBRR0L = 51;
  UCSR0B = (1<<RXEN0)|(1<<TXEN0); //rx and tx enabled
 
    //ADC
  DIDR0 = _BV(ADC0D) |  _BV(AIN0D) |  _BV(AIN1D);  //digital input disabled for ADC and COMP
  ADCSRA = _BV(ADPS2) | _BV(ADEN); //prescaler=16 250KHz
  ADMUX = _BV(REFS0);

}

void loop()
{
   //trigger when input high
    while ( ( ACSR & (1<<ACO)) ){} // Wait for low COMPERATOR output
    while ( !( ACSR & (1<<ACO)) ){} // Wait for high COMP out
 
      //sample 60 readings
    for(i=0;i<60;++i){   
    ADCSRA |=_BV(ADSC);   //start conversion
    while ( !( ADCSRA & (1<<ADIF)) ){} // Wait for conversion to complete

    out[i]=ADCL;
    dh = ADCH;
    ADCSRA |=_BV(ADIF);   //reset interrupt
    if(range==1){delay(1);}  //slower sweep for low freq
    }
    
      //send 60 bytes
  for(i=0;i<60;++i){ 
    while ( !( UCSR0A & (1<<UDRE0)) ){} // Wait for empty transmit buffer
    UDR0 = out[i];  // send the data
  }
      delay(500);
      
    //check bytes received
  if( (UCSR0A & (1<<RXC0)) ) {range = UDR0 - 49;}

}


