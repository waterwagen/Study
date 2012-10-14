package com.waterwagen.study.fest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.fest.swing.core.ComponentLookupScope;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFestBasics
{
	private FrameFixture mFrameFixture;

	@BeforeClass 
	public static void setUpClass() 
	{
		FailOnThreadViolationRepaintManager.install();
	}
	
	@Before
	public void setUpTest() throws InvocationTargetException, InterruptedException
	{
		TestJFrame test_frame = GuiActionRunner.execute(new GuiQuery<TestJFrame>() 
		{
			protected TestJFrame executeInEDT() 
			{
				return new TestJFrame();
			}
		});
		mFrameFixture = new FrameFixture(test_frame);
		mFrameFixture.robot.settings().componentLookupScope(ComponentLookupScope.ALL);
		mFrameFixture.target.setVisible(true);
	}
	
	@Test
	public void test()
	{
		JButtonFixture button = mFrameFixture.button();
		assertThat(button, notNullValue());

		JLabelFixture label = mFrameFixture.label("label1");
		assertThat(label, notNullValue());
		assertThat(label.text(), is(equalTo("Label One")));

		label = mFrameFixture.label("label2");
		assertThat(label, notNullValue());
		label.requireText("Label Two");
		label.click();
	}
	
	@SuppressWarnings("serial")
	private class TestJFrame extends JFrame
	{
		private JButton mClickMe;
		private JLabel mLabel1;
		private JLabel mLabel2;		
		
		private TestJFrame()
		{
			mClickMe = new JButton("Click Me!");
			add(mClickMe);
			
			mLabel1 = new JLabel("Label One");
			mLabel1.setName("label1");
			mLabel1.addMouseListener(buildPrintingMouseListener("Label 1 was clicked on!"));
			add(mLabel1);
			
			mLabel2 = new JLabel("Label Two");
			mLabel2.setName("label2");
			add(mLabel2);
		}

		private MouseListener buildPrintingMouseListener(final String string)
		{
			return new MouseListener()
			{
				@Override
				public void mouseClicked(MouseEvent arg0)
				{
					System.out.println(string);
				}

				@Override
				public void mouseEntered(MouseEvent arg0)
				{
				}

				@Override
				public void mouseExited(MouseEvent arg0)
				{
				}

				@Override
				public void mousePressed(MouseEvent arg0)
				{
				}

				@Override
				public void mouseReleased(MouseEvent arg0)
				{
				}
			};
		}
	}
}