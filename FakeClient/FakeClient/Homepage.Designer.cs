#define TEST_DEFAULT_VAL
namespace FakeClient
{
    partial class ClienteFake
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.cc_btn = new System.Windows.Forms.Button();
            this.serverAddr = new System.Windows.Forms.TextBox();
            this.console = new System.Windows.Forms.RichTextBox();
            this.SuspendLayout();
            // 
            // cc_btn
            // 
            this.cc_btn.Location = new System.Drawing.Point(188, 121);
            this.cc_btn.Name = "cc_btn";
            this.cc_btn.Size = new System.Drawing.Size(73, 24);
            this.cc_btn.TabIndex = 0;
            this.cc_btn.Text = "Conectar-se";
            this.cc_btn.UseVisualStyleBackColor = true;
            this.cc_btn.Click += new System.EventHandler(this.ccbtn_Click);
            // 
            // serverAddr
            // 
            this.serverAddr.Location = new System.Drawing.Point(161, 73);
            this.serverAddr.Name = "serverAddr";
            this.serverAddr.Size = new System.Drawing.Size(127, 20);
            this.serverAddr.TabIndex = 1;
#if TEST_DEFAULT_VAL
            this.serverAddr.Text = "ftp://speedtest.tele2.net/";
#else
            this.serverAddr.Text = "ftp://";
#endif
            // 
            // console
            // 
            this.console.Location = new System.Drawing.Point(34, 288);
            this.console.Name = "console";
            this.console.ReadOnly = true;
            this.console.Size = new System.Drawing.Size(406, 105);
            this.console.TabIndex = 2;
            this.console.Text = "";
            // 
            // ClienteFake
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(484, 851);
            this.Controls.Add(this.console);
            this.Controls.Add(this.serverAddr);
            this.Controls.Add(this.cc_btn);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.Name = "ClienteFake";
            this.Text = "Cliente Fake";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

#endregion

        private System.Windows.Forms.Button cc_btn;
        private System.Windows.Forms.TextBox serverAddr;
        private System.Windows.Forms.RichTextBox console;
    }
}

